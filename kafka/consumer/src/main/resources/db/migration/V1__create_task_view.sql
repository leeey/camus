-- 같은 이벤트를 두 번 받아도 한 번만 반영하기 위한 처리 이력 (eventId 기준 멱등)
CREATE TABLE processed_event
(
    event_id     UUID PRIMARY KEY,
    event_type   VARCHAR(50) NOT NULL,
    processed_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- task-events 로 만드는 조회용 테이블
CREATE TABLE task_summary
(
    task_id       BIGINT PRIMARY KEY,
    title         VARCHAR(255),
    content       TEXT,
    priority      INTEGER,
    archived      BOOLEAN     NOT NULL DEFAULT FALSE,
    deleted       BOOLEAN     NOT NULL DEFAULT FALSE,
    last_event_at TIMESTAMPTZ NOT NULL,
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);
