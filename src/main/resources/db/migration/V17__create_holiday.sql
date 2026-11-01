CREATE TABLE holiday (
    holiday_date        DATE PRIMARY KEY,
    name                VARCHAR(100) NOT NULL,
    created_by          VARCHAR(50) NOT NULL,
    created_at          TIMESTAMP NOT NULL,
    updated_by          VARCHAR(50) NOT NULL,
    updated_at          TIMESTAMP NOT NULL
);

COMMENT ON TABLE holiday IS '공휴일';
COMMENT ON COLUMN holiday.holiday_date IS '날짜';
COMMENT ON COLUMN holiday.name IS '공휴일명';

INSERT INTO batch_job (job_id, job_name, description, cron_expression, enabled, created_by, created_at, updated_by, updated_at)
VALUES ('HOLIDAY_COLLECT', '공휴일 수집', '한국천문연구원 공휴일 수집', '0 0 5 1 * *', FALSE,
        'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP);
