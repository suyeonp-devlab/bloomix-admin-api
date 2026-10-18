INSERT INTO admin_menu (menu_id, parent_id, menu_name, menu_path, sort_order, enabled, created_by, created_at, updated_by, updated_at)
VALUES
    ('BATCH',         NULL,    '배치 관리', NULL,               3, TRUE, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    ('BATCH_JOB',     'BATCH', '배치 목록', '/batch/jobs',      1, TRUE, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    ('BATCH_HISTORY', 'BATCH', '실행 이력', '/batch/histories', 2, TRUE, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP);
