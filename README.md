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

#### required environment variables
- `JWT_TOKEN_SECRET` : jwt (webmvc, webflux) token signing secret (256 bit 이상 랜덤 값)
- `KEY_STORE_LOCATION` : spring cloud config 암호화 keystore 경로 (기본값 `file:.keystore/camusConfigEncKey.jks`, git 추적 제외)
