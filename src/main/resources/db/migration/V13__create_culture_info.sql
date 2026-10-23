CREATE TABLE culture_info (
    seq                 VARCHAR(20) PRIMARY KEY,
    service_type        VARCHAR(1) NOT NULL,
    service_name        VARCHAR(50),
    realm_name          VARCHAR(50),
    title               VARCHAR(500) NOT NULL,
    start_date          DATE NOT NULL,
    end_date            DATE NOT NULL,
    place               VARCHAR(300),
    area                VARCHAR(50),
    sigungu             VARCHAR(50),
    thumbnail_url       VARCHAR(500),
    longitude           NUMERIC(10, 7),
    latitude            NUMERIC(10, 7),
    price_text          TEXT,
    contents            TEXT,
    view_url            VARCHAR(500),
    phone               VARCHAR(100),
    image_url           VARCHAR(500),
    place_url           VARCHAR(500),
    place_address       VARCHAR(500),
    place_seq           VARCHAR(20),
    detail_synced_at    TIMESTAMP,
    created_by          VARCHAR(50) NOT NULL,
    created_at          TIMESTAMP NOT NULL,
    updated_by          VARCHAR(50) NOT NULL,
    updated_at          TIMESTAMP NOT NULL
);

CREATE INDEX idx_culture_info_detail_synced ON culture_info (detail_synced_at);
CREATE INDEX idx_culture_info_end_date ON culture_info (end_date);
CREATE INDEX idx_culture_info_place ON culture_info (place_seq);

COMMENT ON TABLE culture_info IS '문화정보원 문화정보 연동';
COMMENT ON COLUMN culture_info.seq IS '일련번호 (seq)';
COMMENT ON COLUMN culture_info.service_type IS '서비스구분 (serviceTp)';
COMMENT ON COLUMN culture_info.service_name IS '서비스명 (serviceName)';
COMMENT ON COLUMN culture_info.realm_name IS '분류명 (realmName)';
COMMENT ON COLUMN culture_info.title IS '제목 (title)';
COMMENT ON COLUMN culture_info.start_date IS '시작일 (startDate)';
COMMENT ON COLUMN culture_info.end_date IS '마감일 (endDate)';
COMMENT ON COLUMN culture_info.place IS '장소 (place)';
COMMENT ON COLUMN culture_info.area IS '지역 (area)';
COMMENT ON COLUMN culture_info.sigungu IS '시군구 (sigungu)';
COMMENT ON COLUMN culture_info.thumbnail_url IS '썸네일 (thumbnail)';
COMMENT ON COLUMN culture_info.longitude IS '경도 (gpsX)';
COMMENT ON COLUMN culture_info.latitude IS '위도 (gpsY)';
COMMENT ON COLUMN culture_info.price_text IS '티켓요금 (price)';
COMMENT ON COLUMN culture_info.contents IS '내용 (contents1)';
COMMENT ON COLUMN culture_info.view_url IS '관람URL (url)';
COMMENT ON COLUMN culture_info.phone IS '문의처 (phone)';
COMMENT ON COLUMN culture_info.image_url IS '이미지 (imgUrl)';
COMMENT ON COLUMN culture_info.place_url IS '공연장URL (placeUrl)';
COMMENT ON COLUMN culture_info.place_address IS '공연장주소 (placeAddr)';
COMMENT ON COLUMN culture_info.place_seq IS '문화예술공간 일련번호 (placeSeq)';
COMMENT ON COLUMN culture_info.detail_synced_at IS '상세 수집일시';

CREATE TABLE culture_place (
    place_seq           VARCHAR(20) PRIMARY KEY,
    name                VARCHAR(300) NOT NULL,
    class_name          VARCHAR(100),
    group_name          VARCHAR(100),
    phone               VARCHAR(100),
    homepage_url        VARCHAR(500),
    address             VARCHAR(500),
    zip_code            VARCHAR(10),
    opened_date         VARCHAR(20),
    representative      VARCHAR(100),
    description         TEXT,
    image_url           VARCHAR(500),
    longitude           NUMERIC(10, 7),
    latitude            NUMERIC(10, 7),
    created_by          VARCHAR(50) NOT NULL,
    created_at          TIMESTAMP NOT NULL,
    updated_by          VARCHAR(50) NOT NULL,
    updated_at          TIMESTAMP NOT NULL
);

COMMENT ON TABLE culture_place IS '문화정보원 문화예술공간 연동';
COMMENT ON COLUMN culture_place.place_seq IS '문화예술공간 일련번호 (seq)';
COMMENT ON COLUMN culture_place.name IS '문화예술공간명 (culName)';
COMMENT ON COLUMN culture_place.class_name IS '문화예술공간분류명 (culClName)';
COMMENT ON COLUMN culture_place.group_name IS '문화예술공간구분명 (culGrpName)';
COMMENT ON COLUMN culture_place.phone IS '전화 (culTel)';
COMMENT ON COLUMN culture_place.homepage_url IS '홈페이지 (culHomeUrl)';
COMMENT ON COLUMN culture_place.address IS '주소 (culAddr)';
COMMENT ON COLUMN culture_place.zip_code IS '우편번호 (zipCode)';
COMMENT ON COLUMN culture_place.opened_date IS '개관일 (culOpenDay)';
COMMENT ON COLUMN culture_place.representative IS '대표자명 (culOpenName)';
COMMENT ON COLUMN culture_place.description IS '문화예술공간 소개 (culCont)';
COMMENT ON COLUMN culture_place.image_url IS '전경/구성원 이미지 (culViewImg1)';
COMMENT ON COLUMN culture_place.longitude IS '경도 (gpsX)';
COMMENT ON COLUMN culture_place.latitude IS '위도 (gpsY)';

INSERT INTO batch_job (job_id, job_name, description, cron_expression, enabled, created_by, created_at, updated_by, updated_at)
VALUES ('CULTURE_INFO_COLLECT', '문화정보 수집', '문화정보원 문화정보 목록/상세/문화예술공간 수집', '0 30 3 * * *', FALSE,
        'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP);
