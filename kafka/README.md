### Kafka

#### message & batch
- 메시지 발행 구독 시스템 (message pub/sub system)
- 분산 커밋 로그 (distributed commit log)
- 분산 스트리밍 플랫폼 (distributed streaming platform)
- data 의 기본 단위를 message 라고 한다. byte[] 
- message 는 key 라는 metadata 를 포함할 수 있다.
- message data 는 topic 으로 분류된 partition 에 수록된다.
- 일관된 partition 의 결정을 위해 hash key 를 생성한다.
- 여러 개의 message 를 모아서 batch 형태로 partition 에 수록한다. (효율성)
- batch 는 latency / throughput 과 trade-off 관계
- batch 가 클수록 단위 시간당 throughput 은 good, message 의 latency 는 bad

#### schema
- byte[] message 를 이해하기 쉽도록 구조화한 data type
- json, xml, avro, ...

#### topic & partition
- kafka 의 message 는 topic 으로 분류된다.
- 하나의 topic 은 여러개의 partition 으로 구성될 수 있다.
- commit log data 의 관점에서 partition 은 하나의 log 에 해당한다.
- message 는 partition 에 추가할 수만 있고 맨 앞에서 제일 끝까지의 순서로 읽힌다.
- message 처리 순서는 topic 이 아닌 partition 별로 유지 관리된다.
- stream 은 partition 개수와 관계없이 하나의 topic data 로 간주된다.
- stream 은 producer 로부터 consumer 로 이동되는 연속적인 data 를 나타낸다.

#### producer & consumer
- producer 는 새로운 message 를 생성한다.
- producer 는 message 가 어떤 partition 에 수록되는지 관여하지 않지만, message key 와 partitioner 를 사용하여 특정 partition 에 직접 message 를 작성할 수 있다.
- consumer 는 message 를 읽는다.
- consumer 는 하나 이상의 topic 을 구독하여 message 를 생성된 순서대로 읽는다.
- consumer 는 message 의 offset (sequential integer) 을 유지하여 읽는 메시지의 위치를 알 수 있다.
- consumer 는 consumer group 의 member 로 동작한다.
- 한 topic 을 소비하기 위해 같은 그룹의 여러 consumer 가 함께 동작한다.
- 한 topic 의 각 partition 은 하나의 consumer 만 소비할 수 있다.
- 각 consumer 가 특정 partition 에 대응되는 것을 partition ownership 이라고 한다.
- consumer 의 수평적 확장 (scale-out) 과 한 consumer 가 message 읽기를 실패한 경우 다른 consumer 가 이를 대신 읽어 줄 수 있다.
- 보통 producer 가 consumer 보다 훨씬 더 빠르다.

#### broker & cluster
- 하나의 kafka server 를 broker 라고 한다.
- broker 는 producer 로부터 message 를 수신하여 offset 을 지정한 후 disk 에 저장한다.
- broker 는 consumer 에게 disk 에 저장된 message 를 전송한다.
- broker 는 초당 수천 ~ 수백만 개의 message 를 처리할 수 있다.
- broker 는 cluster 의 일부로 동작하도록 설계되었다.
- n 개의 broker 는 하나의 cluster 에 포함 될 수 있고 그 중 하나는 controller 의 기능을 수행한다.
- controller 는 각 브로커에게 partition 을 할당하고 monitoring 한다.
- partition 을 소유한 broker 를 partition leader 라고 한다.
- 하나의 partition 을 n 개의 broker 에게 지정 될 수도 있는데 해당 partition 은 복제 (replication) 된다.
- 보존 (retention) 은 kafka 의 핵심 기능으로 일정 기간 message 를 보존하는 것이다.
- retention 은 일정기간, 지정된 topic 의 크기 등으로 설정할수 있다.
- topic 마다 retention 설정을 다르게 할 수 있다.
- topic 은 압축 로그 (log compacted) 설정으로 구성될 수도 있으며 이 경우 같은 key 를 갖는 message 들은 가장 최신의 것만 보존된다.

#### multiple cluster
- data type 에 따라 구분해서 처리할 수 있다.
- 보안 요구사항을 분리해서 처리할 수 있다.
- 재해복구를 대비한 다중 데이터센터를 유지할 수 있다.

#### why kafka
- 여러 클라이언트가 많은 topic 이나 같은 topic 을 사용해도 무리없이 많은 producer 의 message 를 처리할 수 있다.
- 많은 consumer 가 상호 간섭 없이 어떤 message stream 도 읽을 수 있게 지원한다.
- consumer 는 consumer group 의 멤버가 되어 message stream 을 공유할 수 있다.
- message 를 지속해서 보존할 수 있다.
- 확장성이 좋아서 어떤 크기의 data 라도 쉽게 처리할 수 있다.

#### use case
- action log
- message transfer
- metrics & logging
- commit log
- stream processing

#### topic config
- num.partitions
  - 보통 topic 당 파티션의 수는 broker 의 수와 맞추거나 배수로 설정한다.
  - 미래의 사용량 예측값을 기준으로 처리량을 계산한다.
  - partition 수 = topic 의 목표 처리량 / consumer 의 예상 처리량
  - 경험상 partitions 의 용량을 6GB 미만으로 유지하는 것이 좋다.
- default.replication.factor
  - 자동 토픽 생성 기능이 활성화된 경우 topic 의 복제 factor 를 결정한다.
  - min.insync.replicas 보다 1 이상 크게 설정하는 것을 권장한다.
  - fault tolerance 를 고려하면 min.insync.replicas 보다 2 큰 값을로 설정하는 것이 좋다.
  - 일반적인 cluster 의 경우 partition 별로 최소한 3개의 replica 를 가져야한다.
- log.retention.ms
  - message 보존 설정 주기
  - hours, minutes, ms
- log.retention.bytes
  - message 보존 설정 용량
  - 최대 용량 = 설정값 * partition 수
  - 영구 보존 값은 -1
- log.segment.bytes
  - segment 의 용량 설정
- log.roll.ms
  - segment 가 닫히기까지의 시간 설정
- min.insync.replicas
  - 2로 잡을 경우 최소한 2개의 replica 가 최신 상태로 producer 와 '동기화' 된다.
  - ack=all 과 함께 사용한다.
  - persistence 가 중요한 경우 높게 설정
  - 성능과 trade-off
- message.max.bytes
  - message 의 최대 크기 설정
  - 기본값은 1,000,000 bytes
  - consumer 의 fetch.message.max.bytes, cluster broker 의 replica.fetch.max.bytes 설정과 맞아야 한다.