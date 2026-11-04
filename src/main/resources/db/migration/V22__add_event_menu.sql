INSERT INTO admin_menu (menu_id, parent_id, menu_name, menu_path, sort_order, enabled, created_by, created_at, updated_by, updated_at)
VALUES
    ('EVENT',       NULL,    '행사 관리', NULL,            4, TRUE, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    ('EVENT_LIST',  'EVENT', '행사 목록', '/event/list',   1, TRUE, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    ('EVENT_VENUE', 'EVENT', '장소 목록', '/event/venues', 2, TRUE, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP);
