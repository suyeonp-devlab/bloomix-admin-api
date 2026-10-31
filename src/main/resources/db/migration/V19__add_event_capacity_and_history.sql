ALTER TABLE event
    ADD COLUMN capacity          INTEGER NOT NULL,
    ADD COLUMN on_sale           BOOLEAN NOT NULL,
    ADD COLUMN sale_stop_reason  VARCHAR(500),
    ADD COLUMN sale_stopped_at   TIMESTAMP;

COMMENT ON COLUMN event.capacity IS '정원';
COMMENT ON COLUMN event.on_sale IS '판매 여부';
COMMENT ON COLUMN event.sale_stop_reason IS '판매 중단 사유';
COMMENT ON COLUMN event.sale_stopped_at IS '판매 중단 일시';

ALTER TABLE event RENAME COLUMN source_synced_at TO source_checked_at;
COMMENT ON COLUMN event.source_checked_at IS '원본 확인 일시';

ALTER TABLE event_venue DROP COLUMN source_synced_at;

CREATE TABLE event_source_change (
    change_id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    event_id    BIGINT NOT NULL REFERENCES event (event_id),
    start_date  DATE NOT NULL,
    end_date    DATE NOT NULL,
    venue_id    BIGINT REFERENCES event_venue (venue_id),
    status      VARCHAR(20) NOT NULL,
    created_by  VARCHAR(50) NOT NULL,
    created_at  TIMESTAMP NOT NULL,
    updated_by  VARCHAR(50) NOT NULL,
    updated_at  TIMESTAMP NOT NULL,
    CONSTRAINT ck_event_source_change_status CHECK (status IN ('PENDING', 'APPLIED', 'IGNORED'))
);

CREATE UNIQUE INDEX uk_event_source_change_pending ON event_source_change (event_id) WHERE status = 'PENDING';
CREATE INDEX idx_event_source_change_event ON event_source_change (event_id);

COMMENT ON TABLE event_source_change IS '행사 원본 변경 후보';
COMMENT ON COLUMN event_source_change.change_id IS '변경ID';
COMMENT ON COLUMN event_source_change.event_id IS '행사ID';
COMMENT ON COLUMN event_source_change.start_date IS '원본 시작일';
COMMENT ON COLUMN event_source_change.end_date IS '원본 종료일';
COMMENT ON COLUMN event_source_change.venue_id IS '원본 장소ID';
COMMENT ON COLUMN event_source_change.status IS '상태';

CREATE TABLE event_history (
    history_id        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    event_id          BIGINT NOT NULL REFERENCES event (event_id),
    title             VARCHAR(500) NOT NULL,
    start_date        DATE NOT NULL,
    end_date          DATE NOT NULL,
    venue_id          BIGINT,
    capacity          INTEGER NOT NULL,
    on_sale           BOOLEAN NOT NULL,
    sale_stop_reason  VARCHAR(500),
    enabled           BOOLEAN NOT NULL,
    verified          BOOLEAN NOT NULL,
    created_by        VARCHAR(50) NOT NULL,
    created_at        TIMESTAMP NOT NULL,
    updated_by        VARCHAR(50) NOT NULL,
    updated_at        TIMESTAMP NOT NULL
);

CREATE INDEX idx_event_history_event ON event_history (event_id);

COMMENT ON TABLE event_history IS '행사 이력';
COMMENT ON COLUMN event_history.history_id IS '이력ID';
COMMENT ON COLUMN event_history.event_id IS '행사ID';
COMMENT ON COLUMN event_history.title IS '제목';
COMMENT ON COLUMN event_history.start_date IS '시작일';
COMMENT ON COLUMN event_history.end_date IS '종료일';
COMMENT ON COLUMN event_history.venue_id IS '장소ID';
COMMENT ON COLUMN event_history.capacity IS '정원';
COMMENT ON COLUMN event_history.on_sale IS '판매 여부';
COMMENT ON COLUMN event_history.sale_stop_reason IS '판매 중단 사유';
COMMENT ON COLUMN event_history.enabled IS '노출 여부';
COMMENT ON COLUMN event_history.verified IS '확인 여부';

CREATE TABLE event_venue_history (
    history_id  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    venue_id    BIGINT NOT NULL REFERENCES event_venue (venue_id),
    name        VARCHAR(300) NOT NULL,
    address     VARCHAR(500),
    sido_code   VARCHAR(50),
    sigungu     VARCHAR(50),
    latitude    NUMERIC(10, 7),
    longitude   NUMERIC(10, 7),
    verified    BOOLEAN NOT NULL,
    created_by  VARCHAR(50) NOT NULL,
    created_at  TIMESTAMP NOT NULL,
    updated_by  VARCHAR(50) NOT NULL,
    updated_at  TIMESTAMP NOT NULL
);

CREATE INDEX idx_event_venue_history_venue ON event_venue_history (venue_id);

COMMENT ON TABLE event_venue_history IS '행사 장소 이력';
COMMENT ON COLUMN event_venue_history.history_id IS '이력ID';
COMMENT ON COLUMN event_venue_history.venue_id IS '장소ID';
COMMENT ON COLUMN event_venue_history.name IS '장소명';
COMMENT ON COLUMN event_venue_history.address IS '주소';
COMMENT ON COLUMN event_venue_history.sido_code IS '시도';
COMMENT ON COLUMN event_venue_history.sigungu IS '시군구';
COMMENT ON COLUMN event_venue_history.latitude IS '위도';
COMMENT ON COLUMN event_venue_history.longitude IS '경도';
COMMENT ON COLUMN event_venue_history.verified IS '확인 여부';
