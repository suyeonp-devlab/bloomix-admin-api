INSERT INTO admin_menu (menu_id, parent_id, menu_name, menu_path, sort_order, enabled, created_by, created_at, updated_by, updated_at)
VALUES
    ('USER',      NULL,   '회원 관리', NULL,     2, TRUE, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    ('USER_LIST', 'USER', '회원 목록', '/users', 1, TRUE, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP);
