UPDATE admin_menu
SET menu_path = '/user/list',
    updated_by = 'system',
    updated_at = CURRENT_TIMESTAMP
WHERE menu_id = 'USER_LIST'
AND parent_id = 'USER';
