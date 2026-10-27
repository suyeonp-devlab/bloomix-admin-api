ALTER TABLE event_venue DROP CONSTRAINT ck_event_venue_source_type;
ALTER TABLE event_venue ADD CONSTRAINT ck_event_venue_source_type
    CHECK (source_type IN ('KOPIS', 'CULTURE_INFO', 'CULTURE_INFO_UNREGISTERED'));
