CREATE TABLE event_schedule (
    schedule_id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    event_id       BIGINT NOT NULL REFERENCES event (event_id),
    schedule_date  DATE NOT NULL,
    start_time     TIME,
    capacity       INTEGER NOT NULL,
    enabled        BOOLEAN NOT NULL,
    created_by     VARCHAR(50) NOT NULL,
    created_at     TIMESTAMP NOT NULL,
    updated_by     VARCHAR(50) NOT NULL,
    updated_at     TIMESTAMP NOT NULL,
    CONSTRAINT uk_event_schedule UNIQUE NULLS NOT DISTINCT (event_id, schedule_date, start_time)
);

COMMENT ON TABLE event_schedule IS '행사 회차';
COMMENT ON COLUMN event_schedule.schedule_id IS '회차ID';
COMMENT ON COLUMN event_schedule.event_id IS '행사ID';
COMMENT ON COLUMN event_schedule.schedule_date IS '회차 날짜';
COMMENT ON COLUMN event_schedule.start_time IS '시작 시간 (NULL: 종일)';
COMMENT ON COLUMN event_schedule.capacity IS '정원';
COMMENT ON COLUMN event_schedule.enabled IS '사용 여부';

INSERT INTO batch_job (job_id, job_name, description, cron_expression, enabled, created_by, created_at, updated_by, updated_at)
VALUES ('EVENT_SCHEDULE_GENERATE', '회차 생성', '오늘부터 30일 뒤까지 행사 회차 생성', '0 0 7 * * *', FALSE,
        'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP);
