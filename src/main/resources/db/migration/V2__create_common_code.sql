CREATE TABLE common_code_group (
    group_code  VARCHAR(50) PRIMARY KEY,
    group_name  VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    use_yn      BOOLEAN NOT NULL DEFAULT TRUE,
    created_by  VARCHAR(50) NOT NULL REFERENCES admin_account (admin_id),
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by  VARCHAR(50) NOT NULL REFERENCES admin_account (admin_id),
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE common_code (
    group_code VARCHAR(50) NOT NULL REFERENCES common_code_group (group_code),
    code       VARCHAR(50) NOT NULL,
    code_name  VARCHAR(100) NOT NULL,
    sort_order INTEGER NOT NULL DEFAULT 0,
    use_yn     BOOLEAN NOT NULL DEFAULT TRUE,
    created_by VARCHAR(50) NOT NULL REFERENCES admin_account (admin_id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50) NOT NULL REFERENCES admin_account (admin_id),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (group_code, code)
);
