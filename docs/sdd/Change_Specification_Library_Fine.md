# Especificación de Cambio - Baseline library-fine

> Este documento describe la especificación funcional actual del servicio `library-fine` tal como se encuentra implementado en el repositorio, sirviendo como línea base para el seguimiento de la metodología SDD.

## Resumen ejecutivo

- **Objetivo**: Proveer la gestión centralizada del ciclo de vida de multas por devolución tardía de libros, garantizando trazabilidad, idempotencia y notificación asíncrona a otros servicios del sistema.
- **Resultado esperado**: Un servicio que genera multas automáticamente al recibir eventos de préstamo, permite su pago y expone una API REST para consulta y gestión.
- **Criterio corto de éxito**: Generación idempotente de multas ante eventos de `library-rental`, transición de estado PENDING → PAID funcional, y publicación confiable de eventos de integración mediante el patrón Outbox.

## Servicio owner

- [x] `library-fine`

## Servicios impactados

- [x] `library-rental` (productor de eventos `BookReturnedEvent` y `RentalOverdueEvent`)
- [x] `library-notification-service` (consumidor de `FineGeneratedEvent` y `FinePaidEvent`)
- [x] `library-user` (referenciado por `userId` en cada multa)

## Contexto

El servicio `library-fine` es el bounded context de Gestión de Multas dentro del sistema de biblioteca digital. Actualmente, cuando un usuario devuelve un libro tarde o su renta vence sin devolución, el sistema necesita generar y registrar una penalización económica, mantener su estado y permitir su liquidación, notificando a los demás servicios interesados de forma asíncrona.

## Regla de negocio

- **Cálculo de multa**: La tarifa es de **1 USD por día de retraso**. El cálculo lo realiza `FineService` a partir de los días de atraso (`daysOverdue`).
- **Idempotencia**: Solo puede existir una multa por renta (`rental_id` único). Si llega un evento duplicado, se ignora silenciosamente.
- **Estado de la multa**: Una multa puede estar en estado `PENDING` o `PAID`.
- **Invariante de monto**: El monto no puede ser negativo.
- **Pago de multa**: Una multa `PAID` no puede volver a pagarse — lanza error de conflicto.
- **Publicación de eventos**: Cada generación y pago de multa debe publicar un evento de integración de forma confiable, incluso ante fallos del broker de mensajería.

## Alcance

### Incluye

- Generación de multas al recibir eventos `BookReturnedEvent` o `RentalOverdueEvent`.
- Gestión de estado de multas (PENDING → PAID).
- Consulta de multa por ID.
- Listado de multas por usuario con filtro opcional por estado PENDING.
- Publicación asíncrona de eventos de integración mediante Outbox Pattern.
- Dead-letter queues para mensajes fallidos.

### No incluye

- Gestión de préstamos (responsabilidad de `library-rental`).
- Gestión de usuarios o autenticación (responsabilidad de `library-user`).
- Reembolsos o reversión de pagos.
- Notificaciones directas al usuario (responsabilidad de `library-notification-service`).

## Contratos y datos impactados

### APIs HTTP

- `GET /fines/{fineId}`: Obtener detalle completo de una multa.
- `POST /fines/{fineId}/pay`: Marcar una multa pendiente como pagada.
- `GET /fines?userId={uuid}&onlyPending={bool}`: Listar multas de un usuario.

### Eventos

**Consumidos:**
- `BookReturnedMessage` (v1): Consumido desde la cola `fine.book-returned`, routing key `rental.rental.book_returned.v1`. Contiene `rentalId`, `userId`, `returnDate`, `dueDate`.
- `RentalOverdueMessage` (v1): Consumido desde la cola `fine.rental-overdue`, routing key `rental.rental.rental_overdue.v1`. Contiene `rentalId`, `userId`, `dueDate`.

**Publicados:**
- `FineGeneratedEvent` (v1): Publicado al generar una multa. Routing key `fine.fine.fine_generated.v1`. Contiene `fineId`, `rentalId`, `userId`, `amount`, `currency`, `correlationId`.
- `FinePaidEvent` (v1): Publicado al pagar una multa. Routing key `fine.fine.fine_paid.v1`. Contiene `fineId`, `userId`, `amount`, `currency`, `paidAt`.

### Persistencia

- Tabla `fines`: Almacena el estado actual y los metadatos de cada multa.
- Tabla `outbox_events`: Almacena eventos pendientes de publicación para garantía de entrega.

## Escenarios

### Generación de multa por devolución tardía
**Dado** un evento `BookReturnedEvent` con `returnDate` posterior a `dueDate`
**Cuando** el consumidor procesa el mensaje
**Entonces** se crea una multa en estado `PENDING` con monto = daysOverdue × 1 USD
**Y** se registra un `FineGeneratedEvent` en la tabla outbox.

### Idempotencia en generación
**Dado** una multa ya existente para un `rentalId`
**Cuando** llega un segundo evento con el mismo `rentalId`
**Entonces** no se crea una nueva multa
**Y** no se registra ningún evento adicional.

### Pago de multa exitoso
**Dado** una multa existente en estado `PENDING`
**Cuando** se invoca `POST /fines/{fineId}/pay`
**Entonces** la multa pasa al estado `PAID` con `paid_at` registrado
**Y** se registra un `FinePaidEvent` en la tabla outbox.

### Intento de pago duplicado
**Dado** una multa existente en estado `PAID`
**Cuando** se invoca `POST /fines/{fineId}/pay`
**Entonces** se retorna HTTP 409 Conflict con detalle del error.

### Consulta de multa inexistente
**Dado** un `fineId` que no existe en el sistema
**Cuando** se invoca `GET /fines/{fineId}`
**Entonces** se retorna HTTP 404 Not Found con `ProblemDetail` RFC 9457.

### Listado de multas pendientes por usuario
**Dado** un usuario con multas en estado PENDING y PAID
**Cuando** se invoca `GET /fines?userId={id}&onlyPending=true`
**Entonces** se retornan únicamente las multas en estado `PENDING`.

## Estrategia de pruebas

- **Unit tests de dominio**: Validación de `Fine`, `Money`, `FineId`, `RentalId`, `UserId` y `FineService` sin dependencias de Spring ni infraestructura.
- **Application tests**: Pruebas de casos de uso con `FineRepositoryInMemory` — sin mocks, sin Spring context.
- **Architecture tests**: Validación de reglas de Clean Architecture con ArchUnit (7 reglas automatizadas sobre dependencias entre capas).
- **Cobertura mínima**: 80% de líneas cubiertas, validado con JaCoCo en cada build de CI.

## Riesgos y mitigaciones

| Riesgo | Impacto | Mitigación |
|---|---|---|
| Duplicación de multas por eventos repetidos | Alto | Restricción UNIQUE en `rental_id` + validación en `GenerateFineUseCase` |
| Pérdida de eventos de integración ante fallo del broker | Alto | Outbox Pattern: evento persiste con la transacción antes de enviarse a RabbitMQ |
| Procesamiento fallido de mensajes RabbitMQ | Medio | Dead-letter exchange `fine.dlx` enruta mensajes fallidos a DLQs para inspección manual |
| Inconsistencia entre estado de multa y evento publicado | Medio | Outbox Pattern garantiza atomicidad entre persistencia del estado y registro del evento |
