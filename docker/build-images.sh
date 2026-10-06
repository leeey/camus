#!/bin/bash
# kubernetes 로 배포하는 애플리케이션 이미지를 만든다.
#   ./docker/build-images.sh [tag]   (기본 tag: local)
set -euo pipefail
cd "$(dirname "$0")/.."

TAG="${1:-local}"

# 이미지 이름:gradle 모듈:jar 경로
APPS=(
  "auth-server|:auth-server|auth-server/build/libs/auth-server.jar"
  "gateway|:spring-cloud:spring-cloud-gateway|spring-cloud/gateway/build/libs/spring-cloud-gateway.jar"
  "task-service|:hexagonal|hexagonal/build/libs/hexagonal.jar"
  "task-consumer|:kafka:kafka-consumer|kafka/consumer/build/libs/kafka-consumer.jar"
)

tasks=()
for app in "${APPS[@]}"; do
  IFS='|' read -r _ module _ <<< "$app"
  tasks+=("${module}:bootJar")
done
./gradlew -q "${tasks[@]}"

for app in "${APPS[@]}"; do
  IFS='|' read -r name _ jar <<< "$app"
  echo "==> camus/${name}:${TAG}"
  docker build -q -f docker/app.Dockerfile --build-arg JAR_FILE="${jar}" -t "camus/${name}:${TAG}" .
done
