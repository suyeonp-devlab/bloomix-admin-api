CREATE TABLE admin_menu (
    menu_id    VARCHAR(50) PRIMARY KEY,
    parent_id  VARCHAR(50) REFERENCES admin_menu (menu_id),
    menu_name  VARCHAR(50) NOT NULL,
    menu_path  VARCHAR(200),
    sort_order INTEGER NOT NULL,
    enabled    BOOLEAN NOT NULL,
    created_by VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_by VARCHAR(50) NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT ck_admin_menu_child_path CHECK (parent_id IS NULL OR menu_path IS NOT NULL)
);

CREATE UNIQUE INDEX uk_admin_menu_path ON admin_menu (menu_path) WHERE menu_path IS NOT NULL;

INSERT INTO admin_menu (menu_id, parent_id, menu_name, menu_path, sort_order, enabled, created_by, created_at, updated_by, updated_at)
VALUES
    ('ADMIN',      NULL,    '어드민',        NULL,           1, TRUE, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    ('ADMIN_MENU', 'ADMIN', '메뉴 관리',     '/admin/menus', 1, TRUE, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    ('ADMIN_CODE', 'ADMIN', '공통코드 관리', '/admin/codes', 2, TRUE, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP);
