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
# 1. 인프라 (postgresql 15432, kafka 9092~9094, schema registry 8081)
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

#### required environment variables
- `JWT_TOKEN_SECRET` : jwt (webmvc, webflux) token signing secret (256 bit 이상 랜덤 값)
- `KEY_STORE_LOCATION` : spring cloud config 암호화 keystore 경로 (기본값 `file:.keystore/camusConfigEncKey.jks`, git 추적 제외)
- `AWS_KMS_KEY_ID` : batch, spring cloud config 에서 사용하는 AWS KMS key id
- `AWS_REGION` : AWS region (기본값 `ap-northeast-2`)
- `TASK_DB_URL`, `TASK_DB_USERNAME`, `TASK_DB_PASSWORD` : hexagonal(task 서비스) PostgreSQL 접속 정보 (기본값 `jdbc:postgresql://localhost:15432/camus`, `camus`/`camus`)
- `TASK_VIEW_DB_URL`, `TASK_VIEW_DB_USERNAME`, `TASK_VIEW_DB_PASSWORD` : kafka consumer 조회용 PostgreSQL 접속 정보 (기본값 `jdbc:postgresql://localhost:15432/camus_task_view`, `camus`/`camus`)
- `KAFKA_BOOTSTRAP_SERVERS`, `SCHEMA_REGISTRY_URL` : kafka 접속 정보 (기본값 `localhost:9092,localhost:9093,localhost:9094`, `http://localhost:8081`)
- `EUREKA_ENABLED`, `EUREKA_URL` : hexagonal 의 eureka 등록 여부와 주소 (기본값 `true`, `http://127.0.0.1:8761/eureka`)
