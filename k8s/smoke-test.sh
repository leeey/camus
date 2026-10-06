#!/bin/bash
# 로컬 kubernetes 배포 확인: 토큰 발급 → gateway → task-service → outbox → kafka → consumer
#   kubectl port-forward -n traefik svc/traefik 18080:80   (다른 터미널)
#   ./k8s/smoke-test.sh
set -euo pipefail

AUTH="${AUTH_URL:-http://auth.localtest.me:18080}"
API="${API_URL:-http://api.localtest.me:18080}"

token=$(curl -sf -u camus-service:camus-service-secret -d grant_type=client_credentials \
  -d 'scope=task.read task.write' "$AUTH/oauth2/token" | sed -E 's/.*"access_token":"([^"]+)".*/\1/')

status=$(curl -s -o /dev/null -w '%{http_code}' "$API/task-service/v1/tasks")
[ "$status" = "401" ] || { echo "토큰 없는 요청이 401 이 아닙니다: $status"; exit 1; }

task_id=$(curl -sf -X POST "$API/task-service/v1/tasks" -H "Authorization: Bearer $token" \
  -H 'Content-Type: application/json' -d '{"title":"smoke","content":"k8s","priorityType":"HIGH"}' \
  | sed -E 's/.*"id":([0-9]+).*/\1/')
echo "task 생성: id=$task_id"

for _ in $(seq 1 30); do
  row=$(kubectl exec -n camus postgres-0 -- psql -U camus -d camus_task_view -tAc \
    "select title from task_summary where task_id = $task_id" 2>/dev/null || true)
  if [ "$row" = "smoke" ]; then
    echo "consumer 조회 테이블 반영 확인"
    exit 0
  fi
  sleep 1
done
echo "consumer 조회 테이블에 반영되지 않았습니다"
exit 1
