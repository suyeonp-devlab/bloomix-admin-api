CREATE TABLE admin_account (
    admin_id      VARCHAR(50) PRIMARY KEY,
    password      VARCHAR(255) NOT NULL,
    pw_init       BOOLEAN NOT NULL DEFAULT TRUE,
    pw_fail_count INTEGER NOT NULL DEFAULT 0,
    status        VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_by    VARCHAR(50),
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by    VARCHAR(50),
    updated_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_admin_account_status CHECK (status IN ('ACTIVE', 'LOCKED', 'DISABLED'))
);
