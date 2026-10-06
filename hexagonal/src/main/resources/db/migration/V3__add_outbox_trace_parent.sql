-- 이벤트를 기록한 요청의 W3C traceparent. relay 가 발행할 때 부모 trace 로 이어 붙인다.
ALTER TABLE outbox_event ADD COLUMN trace_parent VARCHAR(55);
