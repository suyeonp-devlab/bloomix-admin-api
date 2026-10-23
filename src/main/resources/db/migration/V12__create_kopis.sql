CREATE TABLE kopis_performance (
    performance_id      VARCHAR(20) PRIMARY KEY,
    title               VARCHAR(300) NOT NULL,
    genre_name          VARCHAR(50),
    state               VARCHAR(20),
    start_date          DATE NOT NULL,
    end_date            DATE NOT NULL,
    area                VARCHAR(50),
    facility_id         VARCHAR(20),
    facility_name       VARCHAR(300),
    hall_id             VARCHAR(20),
    poster_url          VARCHAR(500),
    cast_members        TEXT,
    crew                TEXT,
    runtime             VARCHAR(100),
    age_limit           VARCHAR(100),
    price_text          TEXT,
    schedule_text       TEXT,
    story               TEXT,
    producer            VARCHAR(300),
    production_company  VARCHAR(300),
    planning_company    VARCHAR(300),
    host                VARCHAR(300),
    organizer           VARCHAR(300),
    open_run            BOOLEAN NOT NULL,
    visit               BOOLEAN NOT NULL,
    child               BOOLEAN NOT NULL,
    daehakro            BOOLEAN NOT NULL,
    festival            BOOLEAN NOT NULL,
    musical_license     BOOLEAN NOT NULL,
    musical_create      BOOLEAN NOT NULL,
    intro_images        JSONB,
    registered_at       TIMESTAMP,
    source_updated_at   TIMESTAMP,
    created_by          VARCHAR(50) NOT NULL,
    created_at          TIMESTAMP NOT NULL,
    updated_by          VARCHAR(50) NOT NULL,
    updated_at          TIMESTAMP NOT NULL
);

CREATE INDEX idx_kopis_performance_facility ON kopis_performance (facility_id);
CREATE INDEX idx_kopis_performance_end_date ON kopis_performance (end_date);

COMMENT ON TABLE kopis_performance IS 'KOPIS 공연 연동';
COMMENT ON COLUMN kopis_performance.performance_id IS '공연ID (mt20id)';
COMMENT ON COLUMN kopis_performance.title IS '공연명 (prfnm)';
COMMENT ON COLUMN kopis_performance.genre_name IS '장르 (genrenm)';
COMMENT ON COLUMN kopis_performance.state IS '공연상태 (prfstate)';
COMMENT ON COLUMN kopis_performance.start_date IS '공연시작일 (prfpdfrom)';
COMMENT ON COLUMN kopis_performance.end_date IS '공연종료일 (prfpdto)';
COMMENT ON COLUMN kopis_performance.area IS '지역 (area)';
COMMENT ON COLUMN kopis_performance.facility_id IS '공연시설ID (mt10id)';
COMMENT ON COLUMN kopis_performance.facility_name IS '공연시설명 (fcltynm)';
COMMENT ON COLUMN kopis_performance.hall_id IS '공연장ID (mt13id)';
COMMENT ON COLUMN kopis_performance.poster_url IS '포스터이미지경로 (poster)';
COMMENT ON COLUMN kopis_performance.cast_members IS '출연진 (prfcast)';
COMMENT ON COLUMN kopis_performance.crew IS '제작진 (prfcrew)';
COMMENT ON COLUMN kopis_performance.runtime IS '런타임 (prfruntime)';
COMMENT ON COLUMN kopis_performance.age_limit IS '관람연령 (prfage)';
COMMENT ON COLUMN kopis_performance.price_text IS '티켓가격 (pcseguidance)';
COMMENT ON COLUMN kopis_performance.schedule_text IS '공연시간 (dtguidance)';
COMMENT ON COLUMN kopis_performance.story IS '줄거리 (sty)';
COMMENT ON COLUMN kopis_performance.producer IS '기획제작사 (entrpsnm)';
COMMENT ON COLUMN kopis_performance.production_company IS '제작사 (entrpsnmP)';
COMMENT ON COLUMN kopis_performance.planning_company IS '기획사 (entrpsnmA)';
COMMENT ON COLUMN kopis_performance.host IS '주최 (entrpsnmH)';
COMMENT ON COLUMN kopis_performance.organizer IS '주관 (entrpsnmS)';
COMMENT ON COLUMN kopis_performance.open_run IS '오픈런 (openrun)';
COMMENT ON COLUMN kopis_performance.visit IS '내한 (visit)';
COMMENT ON COLUMN kopis_performance.child IS '아동 (child)';
COMMENT ON COLUMN kopis_performance.daehakro IS '대학로 (daehakro)';
COMMENT ON COLUMN kopis_performance.festival IS '축제 (festival)';
COMMENT ON COLUMN kopis_performance.musical_license IS '뮤지컬 라이선스 (musicallicense)';
COMMENT ON COLUMN kopis_performance.musical_create IS '뮤지컬 창작 (musicalcreate)';
COMMENT ON COLUMN kopis_performance.intro_images IS '소개이미지목록 (styurls)';
COMMENT ON COLUMN kopis_performance.registered_at IS '최초등록일 (frstregdt)';
COMMENT ON COLUMN kopis_performance.source_updated_at IS '최종수정일 (updatedate)';

CREATE TABLE kopis_facility (
    facility_id         VARCHAR(20) PRIMARY KEY,
    name                VARCHAR(300) NOT NULL,
    hall_count          INTEGER,
    characteristic      VARCHAR(50),
    opened_year         VARCHAR(10),
    seat_scale          INTEGER,
    phone               VARCHAR(50),
    homepage_url        VARCHAR(500),
    address             VARCHAR(500),
    latitude            NUMERIC(10, 7),
    longitude           NUMERIC(10, 7),
    restaurant          BOOLEAN NOT NULL,
    cafe                BOOLEAN NOT NULL,
    store               BOOLEAN NOT NULL,
    kids_room           BOOLEAN NOT NULL,
    nursing_room        BOOLEAN NOT NULL,
    parking_lot         BOOLEAN NOT NULL,
    barrier_parking     BOOLEAN NOT NULL,
    barrier_restroom    BOOLEAN NOT NULL,
    barrier_ramp        BOOLEAN NOT NULL,
    barrier_elevator    BOOLEAN NOT NULL,
    halls               JSONB,
    created_by          VARCHAR(50) NOT NULL,
    created_at          TIMESTAMP NOT NULL,
    updated_by          VARCHAR(50) NOT NULL,
    updated_at          TIMESTAMP NOT NULL
);

COMMENT ON TABLE kopis_facility IS 'KOPIS 공연시설 연동';
COMMENT ON COLUMN kopis_facility.facility_id IS '공연시설ID (mt10id)';
COMMENT ON COLUMN kopis_facility.name IS '공연시설명 (fcltynm)';
COMMENT ON COLUMN kopis_facility.hall_count IS '공연장수 (mt13cnt)';
COMMENT ON COLUMN kopis_facility.characteristic IS '시설특성 (fcltychartr)';
COMMENT ON COLUMN kopis_facility.opened_year IS '개관연도 (opende)';
COMMENT ON COLUMN kopis_facility.seat_scale IS '객석수 (seatscale)';
COMMENT ON COLUMN kopis_facility.phone IS '전화번호 (telno)';
COMMENT ON COLUMN kopis_facility.homepage_url IS '홈페이지 (relateurl)';
COMMENT ON COLUMN kopis_facility.address IS '주소 (adres)';
COMMENT ON COLUMN kopis_facility.latitude IS '위도 (la)';
COMMENT ON COLUMN kopis_facility.longitude IS '경도 (lo)';
COMMENT ON COLUMN kopis_facility.restaurant IS '레스토랑 (restaurant)';
COMMENT ON COLUMN kopis_facility.cafe IS '카페 (cafe)';
COMMENT ON COLUMN kopis_facility.store IS '편의점 (store)';
COMMENT ON COLUMN kopis_facility.kids_room IS '놀이방 (nolibang)';
COMMENT ON COLUMN kopis_facility.nursing_room IS '수유실 (suyu)';
COMMENT ON COLUMN kopis_facility.parking_lot IS '주차시설 (parkinglot)';
COMMENT ON COLUMN kopis_facility.barrier_parking IS '장애시설-주차장 (parkbarrier)';
COMMENT ON COLUMN kopis_facility.barrier_restroom IS '장애시설-화장실 (restbarrier)';
COMMENT ON COLUMN kopis_facility.barrier_ramp IS '장애시설-경사로 (runwbarrier)';
COMMENT ON COLUMN kopis_facility.barrier_elevator IS '장애시설-엘리베이터 (elevbarrier)';
COMMENT ON COLUMN kopis_facility.halls IS '공연장목록 (mt13s)';

INSERT INTO batch_job (job_id, job_name, description, cron_expression, enabled, created_by, created_at, updated_by, updated_at)
VALUES ('KOPIS_COLLECT', 'KOPIS 공연 수집', 'KOPIS 공연목록/상세/공연시설 수집', '0 0 3 * * *', FALSE,
        'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP);
