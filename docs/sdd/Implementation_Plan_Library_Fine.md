# System Design Document (SDD) - library-fine Implementation

> Este documento detalla el diseño técnico y las decisiones de implementación del microservicio `library-fine`, siguiendo los principios de Clean Architecture y DDD definidos en el Charter del proyecto.

## 1. Arquitectura del Sistema

El servicio sigue estrictamente la Clean Architecture, dividiéndose en las siguientes capas:

### 1.1 Capa de Dominio (`domain`)
- **Entidades**: `Fine` es el Aggregate Root. Posee factory methods `generate()` y `reconstitute()` y encapsula las invariantes del negocio (estado, monto no negativo, transición PENDING → PAID).
- **Value Objects**: `FineId`, `RentalId`, `UserId`, `Money`. Garantizan validación en construcción y son inmutables.
- **Repositorios**: `FineRepository` (interfaz) define las operaciones de persistencia necesarias para el dominio.
- **Domain Services**: `FineService` encapsula el cálculo del monto de la multa (1 USD por día de retraso).
- **Eventos de Dominio**: `FineGeneratedEvent` y `FinePaidEvent` (Java records), publicados tras cada transición de estado.
- **Excepciones**: `FineNotFoundException` y `FineAlreadyPaidException`.

### 1.2 Capa de Aplicación (`application`)
- **Casos de Uso**:
    - `GenerateFineUseCase`: Orquesta la creación idempotente de multas (verifica `existsByRentalId` antes de persistir).
    - `PayFineUseCase`: Gestiona la transición de estado PENDING → PAID y publica el evento correspondiente.
    - `GetFineUseCase`: Recupera el detalle de una multa por su identificador.
    - `GetUserFinesUseCase`: Lista las multas de un usuario con filtro opcional por estado PENDING.
- **DTOs**: `GenerateFineCommand`, `PayFineCommand`, `FineResponse`. Separan el contrato de aplicación del dominio.
- **Puertos**: `FineEventPublisher` define la interfaz de salida para la publicación de eventos.

### 1.3 Capa de Infraestructura (`infrastructure`)
- **Persistencia**:
    - `FineRepositoryImpl`: Implementa el repositorio de dominio usando `FineJpaRepository`.
    - `FineJpaEntity`: Mapeo a base de datos relacional.
    - `FineMapper`: Conversión bidireccional entre modelo de dominio y entidad JPA.
- **Mensajería**:
    - `OutboxFineEventPublisher`: Implementa `FineEventPublisher` guardando eventos en `OutboxJpaEntity` dentro de la misma transacción que la multa.
    - `OutboxPublisherJob`: Proceso `@Scheduled` que cada 5 segundos publica los eventos pendientes a RabbitMQ y marca cada uno como `published = true`.
    - `BookReturnedEventConsumer`: Escucha mensajes `BookReturnedMessage` del exchange `rental`.
    - `RentalOverdueEventConsumer`: Escucha mensajes `RentalOverdueMessage` del exchange `rental`.
- **Configuración**: `RabbitMQConfig` declara exchanges, colas, bindings, dead-letter exchange y el conversor Jackson. `BeanConfig` y `OpenApiConfig` completan la configuración de la aplicación.

### 1.4 Capa de Interfaces (`interfaces`)
- **REST**: `FineController` expone los endpoints HTTP bajo `/fines`.
- **Manejo de Errores**: `GlobalExceptionHandler` traduce excepciones de dominio a respuestas RFC 9457 `ProblemDetail` con códigos HTTP apropiados (404, 409, 400).

## 2. Decisiones de Diseño Clave

### 2.1 Patrón Outbox
Para garantizar consistencia entre la persistencia de la multa y la publicación del evento de integración, los eventos se guardan en la tabla `outbox_events` dentro de la misma transacción de base de datos. Un scheduler los publica a RabbitMQ de forma asíncrona, asegurando entrega *at-least-once* incluso ante fallos del broker.

### 2.2 Idempotencia en la Generación de Multas
La restricción `UNIQUE` sobre `rental_id` en la tabla `fines` y la validación previa en `GenerateFineUseCase` garantizan que nunca se generen dos multas para la misma renta, independientemente de cuántas veces llegue el evento disparador.

### 2.3 Validación de Dominio
Las invariantes (monto no negativo, moneda no nula, transición de estado válida) residen en el núcleo del dominio a través de los Value Objects y el Aggregate Root `Fine`. El sistema nunca puede llegar a un estado inválido sin importar la interfaz de entrada.

### 2.4 Desacoplamiento de Mensajería
El dominio y la aplicación no dependen de RabbitMQ. La interfaz `FineEventPublisher` es implementada por la infraestructura (`OutboxFineEventPublisher`), manteniendo la inversión de dependencias.

### 2.5 Dead-Letter Queues
Los mensajes consumidos que fallen su procesamiento son enrutados automáticamente al exchange `fine.dlx` hacia colas DLQ (`fine.book-returned.dlq`, `fine.rental-overdue.dlq`) para inspección manual, evitando pérdida silenciosa de mensajes.

## 3. Modelo de Datos (Esquema SQL)

### Tabla `fines`
- `fine_id`: UUID (PK)
- `rental_id`: UUID (UNIQUE) — garantiza idempotencia
- `user_id`: UUID
- `amount`: NUMERIC(10, 2) — CHECK amount >= 0
- `currency`: VARCHAR(3)
- `status`: VARCHAR(20) — CHECK IN ('PENDING', 'PAID')
- `generated_at`: TIMESTAMPTZ
- `paid_at`: TIMESTAMPTZ (nullable)
- Índice compuesto: `idx_fines_user_status (user_id, status)`

### Tabla `outbox_events`
- `event_id`: UUID (PK)
- `event_type`: VARCHAR(100)
- `routing_key`: VARCHAR(200)
- `exchange`: VARCHAR(100)
- `payload`: TEXT (JSON serializado)
- `occurred_at`: TIMESTAMPTZ
- `published`: BOOLEAN DEFAULT FALSE
- `published_at`: TIMESTAMPTZ (nullable)
- Índice parcial: `idx_outbox_unpublished (published, occurred_at) WHERE published = FALSE`

## 4. Diagrama de Flujo: Generación de Multa

1. `BookReturnedEventConsumer` o `RentalOverdueEventConsumer` recibe el mensaje de RabbitMQ.
2. Calcula `daysOverdue` a partir de la diferencia entre fecha de devolución y fecha límite.
3. Construye un `GenerateFineCommand` con `rentalId`, `userId` y `daysOverdue`.
4. `GenerateFineUseCase.execute()`:
    a. Verifica que no exista ya una multa para ese `rentalId` (idempotencia).
    b. Delega el cálculo del monto a `FineService`.
    c. Crea la entidad `Fine` vía `Fine.generate()`.
    d. `FineRepository.save(fine)`.
    e. `FineEventPublisher.publish(FineGeneratedEvent)`.
5. La infraestructura guarda el evento en `outbox_events` dentro de la misma transacción.
6. El `OutboxPublisherJob` detecta el nuevo evento y lo envía al exchange `fine` con routing key `fine.fine.fine_generated.v1`.

## 5. Diagrama de Flujo: Pago de Multa

1. `FineController` recibe `POST /fines/{fineId}/pay`.
2. `PayFineUseCase.execute(PayFineCommand)`:
    a. Recupera la multa por `fineId` (lanza `FineNotFoundException` si no existe).
    b. Invoca `fine.pay()` en el Aggregate Root (lanza `FineAlreadyPaidException` si ya estaba pagada).
    c. `FineRepository.save(fine)` persiste el estado PAID y `paid_at`.
    d. `FineEventPublisher.publish(FinePaidEvent)`.
3. La infraestructura guarda el evento en `outbox_events` dentro de la misma transacción.
4. El `OutboxPublisherJob` publica el evento con routing key `fine.fine.fine_paid.v1`.
5. `FineController` retorna `200 OK` con el detalle de la multa pagada.
