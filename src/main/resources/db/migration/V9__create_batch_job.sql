CREATE TABLE batch_job (
    job_id          VARCHAR(50) PRIMARY KEY,
    job_name        VARCHAR(100) NOT NULL,
    description     VARCHAR(255),
    cron_expression VARCHAR(50),
    enabled         BOOLEAN NOT NULL,
    created_by      VARCHAR(50) NOT NULL,
    created_at      TIMESTAMP NOT NULL,
    updated_by      VARCHAR(50) NOT NULL,
    updated_at      TIMESTAMP NOT NULL
);

CREATE TABLE batch_job_history (
    history_id       BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    job_id           VARCHAR(50) NOT NULL REFERENCES batch_job (job_id),
    job_execution_id BIGINT,
    trigger_type     VARCHAR(20) NOT NULL,
    parameters       JSONB,
    status           VARCHAR(20) NOT NULL,
    started_at       TIMESTAMP NOT NULL,
    ended_at         TIMESTAMP,
    message          VARCHAR(1000),
    created_by       VARCHAR(50) NOT NULL,
    created_at       TIMESTAMP NOT NULL,
    updated_by       VARCHAR(50) NOT NULL,
    updated_at       TIMESTAMP NOT NULL,
    CONSTRAINT ck_batch_job_history_trigger_type CHECK (trigger_type IN ('SCHEDULED', 'MANUAL', 'RESTART')),
    CONSTRAINT ck_batch_job_history_status CHECK (status IN ('RUNNING', 'COMPLETED', 'FAILED'))
);

CREATE UNIQUE INDEX uk_batch_job_history_running ON batch_job_history (job_id) WHERE status = 'RUNNING';
CREATE INDEX idx_batch_job_history_job_started ON batch_job_history (job_id, started_at DESC);
