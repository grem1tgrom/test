# ExploreWithMe — Microservices (Spring Cloud)

## Архитектура

Проект переведён на микросервисную архитектуру и готов к запуску в облачной среде.

### Инфраструктурные сервисы (module `infra`)
- **discovery-server** — Spring Cloud Eureka (реестр/обнаружение сервисов)
- **config-server** — Spring Cloud Config Server (централизованные конфиги)
- **gateway-server** — Spring Cloud Gateway (единая точка входа)

### Бизнес-сервисы (module `core`)
- **event-service** — управление мероприятиями (поиск/просмотр/создание/изменение)
- **request-service** — управление заявками на участие
- **user-service** — администрирование пользователями
- **category-service** — категории
- **comment-service** — комментарии
- **interaction-api** — общий модуль для межсервисного взаимодействия (Feign-клиенты + DTO/контракты)

### Статистика (module `stats`)
- **stats-server** — сервис статистики
- **stats-client / stats-dto** — клиент и DTO для общения со статистикой

## Взаимодействие сервисов

- Все сервисы регистрируются в **Eureka**.
- Все сервисы получают конфигурацию из **Config Server** (через discovery).
- Внешние запросы идут только через **Gateway** (порт `8080`).
- Межсервисное взаимодействие реализовано через **OpenFeign** с резервацией/устойчивостью через **Resilience4j** (fallbackFactory).

## Где лежат конфигурации

Конфиги хранятся в `infra/config-server/src/main/resources/config/**`:

- `config/infra/gateway-server/application.yaml` — конфиг gateway (порт 8080 + маршруты)
- `config/infra/config-server/application.yaml` — конфиг config-server (native)
- `config/infra/discovery-server/application.yaml` — конфиг discovery-server
- `config/core/<service-name>/application.yaml` — конфиги core-сервисов
- `config/stats/stats-server/application.yaml` — конфиг stats-server

> В большинстве сервисов используется `server.port: 0` (случайный порт), чтобы запускать несколько инстансов.

## Внутренний API (межсервисный)

Внутренние вызовы выполняются через Feign-клиенты из модуля `core/interaction-api`, например:
- `@FeignClient(name = "event-service", ...)`
- `@FeignClient(name = "user-service", ...)`
- `@FeignClient(name = "request-service", ...)`
- `@FeignClient(name = "category-service", ...)`
- `@FeignClient(name = "comment-service", ...)`

Адрес сервиса определяется через Eureka (serviceId = имя приложения).

## Внешний API

Внешний API доступен через **Gateway**: `http://localhost:8080/**`

Спецификация внешнего API:
- ссылка на swagger/openapi из группового проекта (вставьте ссылку сюда)

## Запуск

### Локально (IDE)
Рекомендуемый порядок запуска:
1. `infra/discovery-server`
2. `infra/config-server`
3. `infra/gateway-server`
4. `stats/stats-server`
5. core-сервисы: `event-service`, `user-service`, `request-service`, `category-service`, `comment-service`

После старта:
- Eureka: `http://localhost:8761`
- Gateway: `http://localhost:8080`

### Через Docker Compose
Если есть `docker-compose.yml`, запуск:
```bash
docker compose up --build
```

## Тестирование (Postman)

- Все Postman-тесты должны отправляться **только на Gateway**: `http://localhost:8080`
- Тесты не должны зависеть от внутренних портов/адресов сервисов.

## Надёжность (устойчивость к сбоям)

Проверка:
1. Запустить все сервисы и прогнать Postman.
2. По одному останавливать микросервисы.
3. Оставить только сервис мероприятий (event-service) и убедиться:
    - запросы, не зависящие критически от остановленных сервисов, продолжают работать;
    - при отсутствии данных других сервисов используются значения по умолчанию (например `0`).

При необходимости применяется **Resilience4j Retry/CircuitBreaker** и fallback в Feign-клиентах.