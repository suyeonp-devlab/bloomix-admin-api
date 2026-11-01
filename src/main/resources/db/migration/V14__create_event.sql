CREATE TABLE event_venue (
    venue_id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    source_type         VARCHAR(20) NOT NULL,
    source_id           VARCHAR(20) NOT NULL,
    name                VARCHAR(300) NOT NULL,
    address             VARCHAR(500),
    sido_code           VARCHAR(50),
    sigungu             VARCHAR(50),
    latitude            NUMERIC(10, 7),
    longitude           NUMERIC(10, 7),
    phone               VARCHAR(100),
    homepage_url        VARCHAR(500),
    seat_scale          INTEGER,
    verified            BOOLEAN NOT NULL,
    source_synced_at    TIMESTAMP NOT NULL,
    created_by          VARCHAR(50) NOT NULL,
    created_at          TIMESTAMP NOT NULL,
    updated_by          VARCHAR(50) NOT NULL,
    updated_at          TIMESTAMP NOT NULL,
    CONSTRAINT uk_event_venue_source UNIQUE (source_type, source_id),
    CONSTRAINT ck_event_venue_source_type CHECK (source_type IN ('KOPIS', 'CULTURE_INFO'))
);

CREATE INDEX idx_event_venue_sido ON event_venue (sido_code);

COMMENT ON TABLE event_venue IS '행사 장소';
COMMENT ON COLUMN event_venue.venue_id IS '장소ID';
COMMENT ON COLUMN event_venue.source_type IS '출처';
COMMENT ON COLUMN event_venue.source_id IS '출처ID';
COMMENT ON COLUMN event_venue.name IS '장소명';
COMMENT ON COLUMN event_venue.address IS '주소';
COMMENT ON COLUMN event_venue.sido_code IS '시도';
COMMENT ON COLUMN event_venue.sigungu IS '시군구';
COMMENT ON COLUMN event_venue.latitude IS '위도';
COMMENT ON COLUMN event_venue.longitude IS '경도';
COMMENT ON COLUMN event_venue.phone IS '전화번호';
COMMENT ON COLUMN event_venue.homepage_url IS '홈페이지';
COMMENT ON COLUMN event_venue.seat_scale IS '객석수';
COMMENT ON COLUMN event_venue.verified IS '확인 여부';
COMMENT ON COLUMN event_venue.source_synced_at IS '출처 반영일시';

CREATE TABLE event (
    event_id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    source_type         VARCHAR(20) NOT NULL,
    source_id           VARCHAR(20) NOT NULL,
    category            VARCHAR(20) NOT NULL,
    genre_name          VARCHAR(50),
    title               VARCHAR(500) NOT NULL,
    start_date          DATE NOT NULL,
    end_date            DATE NOT NULL,
    venue_id            BIGINT REFERENCES event_venue (venue_id),
    poster_url          VARCHAR(500),
    intro_images        JSONB,
    price_text          TEXT,
    description         TEXT,
    cast_members        TEXT,
    runtime             VARCHAR(100),
    age_limit           VARCHAR(100),
    enabled             BOOLEAN NOT NULL,
    verified            BOOLEAN NOT NULL,
    source_synced_at    TIMESTAMP NOT NULL,
    created_by          VARCHAR(50) NOT NULL,
    created_at          TIMESTAMP NOT NULL,
    updated_by          VARCHAR(50) NOT NULL,
    updated_at          TIMESTAMP NOT NULL,
    CONSTRAINT uk_event_source UNIQUE (source_type, source_id),
    CONSTRAINT ck_event_source_type CHECK (source_type IN ('KOPIS', 'CULTURE_INFO')),
    CONSTRAINT ck_event_category CHECK (category IN ('PERFORMANCE', 'EXHIBITION', 'FESTIVAL', 'EXPERIENCE'))
);

CREATE INDEX idx_event_category_end_date ON event (category, end_date);
CREATE INDEX idx_event_end_date ON event (end_date);
CREATE INDEX idx_event_venue ON event (venue_id);

COMMENT ON TABLE event IS '행사';
COMMENT ON COLUMN event.event_id IS '행사ID';
COMMENT ON COLUMN event.source_type IS '출처';
COMMENT ON COLUMN event.source_id IS '출처ID';
COMMENT ON COLUMN event.category IS '카테고리';
COMMENT ON COLUMN event.genre_name IS '장르명';
COMMENT ON COLUMN event.title IS '제목';
COMMENT ON COLUMN event.start_date IS '시작일';
COMMENT ON COLUMN event.end_date IS '종료일';
COMMENT ON COLUMN event.venue_id IS '장소ID';
COMMENT ON COLUMN event.poster_url IS '포스터 이미지';
COMMENT ON COLUMN event.intro_images IS '소개 이미지 목록';
COMMENT ON COLUMN event.price_text IS '가격 안내';
COMMENT ON COLUMN event.description IS '소개';
COMMENT ON COLUMN event.cast_members IS '출연진';
COMMENT ON COLUMN event.runtime IS '런타임';
COMMENT ON COLUMN event.age_limit IS '관람연령';
COMMENT ON COLUMN event.enabled IS '노출 여부';
COMMENT ON COLUMN event.verified IS '확인 여부';
COMMENT ON COLUMN event.source_synced_at IS '출처 반영일시';

INSERT INTO batch_job (job_id, job_name, description, cron_expression, enabled, created_by, created_at, updated_by, updated_at)
VALUES ('EVENT_SYNC', '행사 반영', 'KOPIS·문화정보원 연동 데이터를 행사 장소/행사로 반영', '0 0 6 * * *', FALSE,
        'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP);
