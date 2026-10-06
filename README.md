### boilerplate project for robust application

#### spring cloud
- config 
- service discovery
- gateway

#### observability
- open feign
- docker compose
- prometheus + grafana loki
- mashup (api aggregation)
- apis (member, task)

#### hexagonal
- web (controller)
- usecase
- port
- domain (service)
- infra (adapter)

#### database
- jpa
- r2dbc
- redis
- graphql

#### reactive
- router function
- handler function

#### aws
- kms client
- s3 client
- cloudfront client
- dynamodb client

#### kafka
- producer
- consumer
- avro

#### batch

#### jwt
- webmvc
- webflux

#### reference flow (gateway → task 서비스 → outbox → kafka → consumer)

```
client ─▶ gateway ─▶ task 서비스(hexagonal) ─┬─ task         ┐ 같은 트랜잭션
          (eureka)                            └─ outbox_event ┘
                       outbox relay ─▶ kafka `task-events` (key = taskId)
                                            │
                       consumer ◀───────────┘
                         └ 같은 트랜잭션: processed_event(eventId) + task_summary 갱신
                         └ 처리 실패: 지수 백오프 3회 재시도 후 `task-events.DLT`
```

- **transactional outbox** : task 변경과 이벤트 기록을 한 트랜잭션으로 묶어, DB 저장과 이벤트 발행 중 하나만 성공하는 일을 막는다.
- **outbox relay** : `outbox_event` 를 주기적으로 읽어 kafka 응답(ack)을 확인한 뒤 `published_at` 을 기록한다. postgresql advisory lock 으로 한 번에 한 인스턴스만 발행해 순서를 지킨다.
- **at-least-once** : 발행 후 `published_at` 기록 전에 장애가 나면 같은 이벤트가 다시 발행된다. consumer 는 `eventId` 로 멱등 처리한다.
- **순서** : key 가 taskId 라서 같은 task 의 이벤트는 같은 partition 에서 순서대로 처리된다. 늦게 도착한 이벤트는 `last_event_at` 비교로 최신 상태를 덮어쓰지 않는다.
- **topic** : `task-events` partitions 3, replicas 3, `min.insync.replicas` 2 (`camus.kafka.topics.*` 로 변경)
- **대안** : 트래픽이 많으면 polling relay 대신 debezium CDC(kafka connect) 로 outbox 를 발행하는 방식을 검토한다.

로컬 실행

```shell
# 1. 인프라 (postgresql 15432, kafka 9092~9094, schema registry 8081, redis 6379)
docker compose up -d

# 2. 애플리케이션 (각각 별도 터미널)
./gradlew :spring-cloud:spring-cloud-service-discovery:bootRun   # eureka 8761
./gradlew :hexagonal:bootRun                                      # task 서비스 8084
./gradlew :kafka:kafka-consumer:bootRun                           # consumer 9091
./gradlew :spring-cloud:spring-cloud-gateway:bootRun              # gateway 8000

# 3. gateway 로 호출
curl -X POST localhost:8000/task-service/v1/tasks -H 'Content-Type: application/json' \
  -d '{"title":"hello","content":"world","priorityType":"HIGH"}'

# 4. consumer 조회 테이블 확인
docker exec camus-postgres psql -U camus -d camus_task_view -c 'select * from task_summary'
```

#### auth-server (spring authorization server)

OAuth2 / OIDC 인증 서버 (포트 9400). access token 은 RS256 JWT 이고, resource server 는 공개키(JWKS)만으로 검증한다.

| client | grant | 용도 |
|---|---|---|
| `camus-web` | authorization code + PKCE, refresh token | 사용자 로그인 (secret 을 가진 서버(BFF) 가 토큰을 다룬다) |
| `camus-service` | client credentials | 서비스 간 호출 |

- 엔드포인트 : `/.well-known/openid-configuration`, `/oauth2/authorize`, `/oauth2/token`, `/oauth2/jwks`, `/oauth2/revoke`, `/oauth2/introspect`, `/userinfo`
- access token 15분 (서비스 토큰 5분), refresh token 은 한 번 쓰면 새로 발급하고 이미 쓴 토큰은 거절한다.
- 발급한 인가 정보는 PostgreSQL(`camus_auth`) 에 저장해 인스턴스를 여러 개 띄울 수 있다.
- 서명 키는 `AUTH_SIGNING_PRIVATE_KEY`(PKCS#8 PEM) 로 넣는다. 비어 있으면 기동할 때마다 임시 키를 만든다 (로컬 전용). 키를 바꿀 때는 `AUTH_SIGNING_KEY_ID` 도 바꾼다.
- 사용자(`camus.auth.users`)와 클라이언트는 설정으로 등록한 예시다. 운영에서는 사용자 DB 나 외부 IdP 로 바꾼다.
- 기존 postgresql volume 에는 `camus_auth` 데이터베이스가 없으므로 `docker compose down -v` 후 다시 띄우거나 직접 만든다.

```shell
# 서비스 토큰 (client credentials, 로컬 기본 secret)
curl -u camus-service:camus-service-secret -d grant_type=client_credentials -d scope=task.read localhost:9400/oauth2/token
```

#### resilience (장애 대응)

| 위치 | timeout | retry | circuit breaker | 그 밖에 |
|---|---|---|---|---|
| gateway → 하위 서비스 | connect 2s, response 5s | GET 만 2회 (502/503/504, 연결 오류) | 라우트별, 열리면 503 fallback | 클라이언트 IP 별 rate limit (redis, 초과 시 429) |
| mashup → member, task (feign) | connect 1s, read 2s | 3회 (5xx, 연결 오류/timeout), 지수 백오프 + jitter | 실패율·느린 호출 50% 초과 시 open 10s | task 장애 시 부분 응답 (`degraded: true`) |

- 4xx 는 요청 오류라서 재시도하지 않고 circuit breaker 실패로도 세지 않는다.
- 재시도는 한 곳에서만 한다 (feign 자체 재시도는 끈다). 여러 층에서 재시도하면 횟수가 곱해진다.
- gateway 에서 rate limit 은 라우트의 첫 필터로 둔다. 재시도보다 뒤에 두면 gateway 의 재시도까지 클라이언트 토큰을 쓴다.
- redis 가 응답하지 않으면 rate limiter 는 요청을 통과시킨다 (fail open).
- 동시 호출 수 제한이 필요하면 resilience4j bulkhead 를 추가한다.

#### observability (관측성)

`project.camus.observability-conventions` 를 적용한 서비스(gateway, example-api, hexagonal, kafka consumer, observability 3종)는 같은 방식으로 trace, 지표, 로그를 남긴다.

- **trace** : W3C `traceparent` 로 전파한다. outbox 에 요청의 traceparent 를 저장해 relay 가 이어 붙이므로 요청 → kafka → consumer 가 하나의 trace 가 된다. gateway 응답 헤더 `Trace-Id` 로 traceId 를 확인할 수 있다.
- **지표** : `/actuator/prometheus` (모든 지표에 `application` 태그, http 요청 지연 histogram)
  - `outbox_events_pending` : 아직 발행하지 않은 outbox 이벤트 수
  - `outbox_events_published_total{result=success|failure}` : outbox 발행 결과
  - `task_events_processed_total{result=applied|duplicate}` : consumer 처리 결과
  - `task_events_dead_letter_total` : DLT 로 보낸 이벤트 수
  - `kafka_consumer_fetch_manager_records_lag_max` : consumer lag
- **로그** : traceId/spanId 가 로그에 함께 남는다. `LOGGING_STRUCTURED_FORMAT_CONSOLE=ecs` 면 JSON(ECS) 으로 출력하고, `OTEL_LOGS_EXPORT_ENABLED=true` 면 OTLP 로도 보낸다.
- **샘플링** : 기본은 전부(1.0) 수집한다. 운영에서는 `TRACING_SAMPLING_PROBABILITY` 를 낮추거나 collector 에서 tail sampling 을 쓴다.

관측 스택 (로컬)

```shell
# 인프라 + otel collector(4317/4318), tempo(3200), loki(3100), prometheus(9090), grafana(3000)
docker compose --profile observability up -d

# 애플리케이션은 trace/로그 export 를 켜고 실행한다
export OTEL_TRACES_EXPORT_ENABLED=true OTEL_LOGS_EXPORT_ENABLED=true
```

- grafana (`admin` / `GRAFANA_ADMIN_PASSWORD`, 기본 `admin`) → `camus` 폴더의 `camus overview` 대시보드
  - 요청 수·5xx 비율·p95 지연, circuit breaker, outbox, consumer 처리·lag, DLT, 로그
  - 로그의 traceId → tempo trace, trace 의 span → 같은 traceId 의 로그로 이동
- prometheus 알림 규칙 (`docker/observability/prometheus/alert-rules.yml`) : InstanceDown, HighErrorRate, CircuitBreakerOpen, OutboxBacklog, DeadLetterEvents, ConsumerLag
  - 알림 전송(alertmanager, slack 등)은 환경에 맞게 붙인다.

#### required environment variables
- `JWT_TOKEN_SECRET` : jwt (webmvc, webflux) token signing secret (256 bit 이상 랜덤 값)
- `KEY_STORE_LOCATION` : spring cloud config 암호화 keystore 경로 (기본값 `file:.keystore/camusConfigEncKey.jks`, git 추적 제외)
- `AWS_KMS_KEY_ID` : batch, spring cloud config 에서 사용하는 AWS KMS key id
- `AWS_REGION` : AWS region (기본값 `ap-northeast-2`)
- `TASK_DB_URL`, `TASK_DB_USERNAME`, `TASK_DB_PASSWORD` : hexagonal(task 서비스) PostgreSQL 접속 정보 (기본값 `jdbc:postgresql://localhost:15432/camus`, `camus`/`camus`)
- `TASK_VIEW_DB_URL`, `TASK_VIEW_DB_USERNAME`, `TASK_VIEW_DB_PASSWORD` : kafka consumer 조회용 PostgreSQL 접속 정보 (기본값 `jdbc:postgresql://localhost:15432/camus_task_view`, `camus`/`camus`)
- `KAFKA_BOOTSTRAP_SERVERS`, `SCHEMA_REGISTRY_URL` : kafka 접속 정보 (기본값 `localhost:9092,localhost:9093,localhost:9094`, `http://localhost:8081`)
- `EUREKA_ENABLED`, `EUREKA_URL` : hexagonal 의 eureka 등록 여부와 주소 (기본값 `true`, `http://127.0.0.1:8761/eureka`)
- `AUTH_ISSUER_URI` : auth-server issuer (기본값 `http://localhost:9400`)
- `AUTH_SIGNING_PRIVATE_KEY`, `AUTH_SIGNING_KEY_ID` : access token 서명 키 (PKCS#8 PEM) 와 kid
- `AUTH_SERVICE_CLIENT_SECRET`, `AUTH_WEB_CLIENT_SECRET` : 클라이언트 secret (`{bcrypt}...`, 로컬 기본값은 `camus-service-secret`, `camus-web-secret`)
- `AUTH_WEB_REDIRECT_URIS` : camus-web redirect uri
- `AUTH_DB_URL`, `AUTH_DB_USERNAME`, `AUTH_DB_PASSWORD` : auth-server PostgreSQL (기본값 `jdbc:postgresql://localhost:15432/camus_auth`, `camus`/`camus`)
- `TASK_SERVICE_URI` : gateway 의 task 서비스 주소 (기본값 `lb://HEXAGONAL`, kubernetes 는 `http://hexagonal:8084` 처럼 service 주소)
- `REDIS_HOST`, `REDIS_PORT` : gateway rate limit 용 redis (기본값 `localhost`, `6379`)
- `GATEWAY_RATE_LIMIT_REPLENISH_RATE`, `GATEWAY_RATE_LIMIT_BURST_CAPACITY` : 클라이언트별 초당 보충 토큰 수와 최대 버스트 (기본값 `10`, `20`)
- `OTEL_TRACES_EXPORT_ENABLED`, `OTEL_EXPORTER_OTLP_TRACES_ENDPOINT` : trace OTLP export 여부와 주소 (기본값 `false`, `http://localhost:4318/v1/traces`)
- `OTEL_LOGS_EXPORT_ENABLED`, `OTEL_EXPORTER_OTLP_LOGS_ENDPOINT` : 로그 OTLP export 여부와 주소 (기본값 `false`, `http://localhost:4318/v1/logs`)
- `TRACING_SAMPLING_PROBABILITY` : trace 샘플링 비율 (기본값 `1.0`)
- `LOGGING_STRUCTURED_FORMAT_CONSOLE` : 콘솔 로그 형식 (`ecs`, `logstash`, `gelf` 중 하나면 JSON)
