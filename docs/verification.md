# Verificación funcional de `library-fine`

Este documento sirve como guía rápida para comprobar que el microservicio y sus dependencias funcionan correctamente después de levantar el entorno con Docker Compose.

## Requisitos previos

- Docker y Docker Compose instalados.
- El stack levantado con `docker compose up -d`.
- Acceso a la API en `http://localhost:8084`.
- Acceso a RabbitMQ Management en `http://localhost:15672`.

## Verificación rápida

### 1. Comprobar contenedores

```bash
docker ps
docker compose ps
```

Debes ver los servicios `library-fine`, `library-fine-postgres` y `library-fine-rabbitmq` en estado `Up`.

### 2. Comprobar salud de la API

```bash
curl http://localhost:8084/actuator/health
```

La respuesta debe indicar `UP`.

### 3. Revisar RabbitMQ

- Abrir `http://localhost:15672`.
- Iniciar sesión con `guest / guest`.
- Confirmar que existen las colas y exchanges definidos por el servicio.

### 4. Probar un flujo funcional

1. Generar una multa desde el evento que corresponda o desde el consumidor si existe un endpoint de prueba.
2. Consultar la multa por `GET /fines/{fineId}`.
3. Pagarla con `POST /fines/{fineId}/pay`.
4. Volver a consultar la multa y confirmar que pasó a `PAID`.

### 5. Confirmar persistencia

```sql
select fine_id, rental_id, status, amount, generated_at, paid_at
from fines
order by generated_at desc;
```

La consulta debe reflejar el cambio de estado y la fecha de pago si ya se ejecutó el flujo.


### 6. Confirmar Outbox

```sql
select event_id, event_type, routing_key, published, occurred_at
from outbox_events
order by occurred_at desc;
```

Al generar o pagar una multa, el registro debe aparecer primero con `published = false` y luego pasar a `true` cuando el publicador procese el evento.

## Señales de que algo está mal

- La API responde `DOWN` o no levanta.
- PostgreSQL o RabbitMQ no aparecen como `healthy`.
- Los eventos quedan atascados en `outbox_events` con `published = false`.
- No se crea el registro en `fines` o el pago no cambia el estado a `PAID`.

## Siguiente nivel

Si quieres automatizar esta verificación, el siguiente paso natural es convertir esta guía en un smoke test o en tests de integración con Testcontainers.
