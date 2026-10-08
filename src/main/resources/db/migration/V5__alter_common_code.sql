ALTER TABLE common_code_group RENAME COLUMN use_yn TO enabled;
ALTER TABLE common_code RENAME COLUMN use_yn TO enabled;
ALTER TABLE common_code ADD COLUMN etc1 VARCHAR(50), ADD COLUMN etc2 VARCHAR(50), ADD COLUMN etc3 VARCHAR(50);
