package com.bloomix.admin.domain.event.entity;

import com.bloomix.admin.domain.BaseEntity;
import com.bloomix.admin.domain.batch.job.eventsync.dto.EventVenueSyncData;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventVenue extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long venueId;

  // 출처
  @Enumerated(EnumType.STRING)
  private EventSourceType sourceType;

  // 출처ID
  private String sourceId;

  // 장소명
  private String name;

  // 주소
  private String address;

  // 시도
  private String sidoCode;

  // 시군구
  private String sigungu;

  // 위도
  private BigDecimal latitude;

  // 경도
  private BigDecimal longitude;

  // 전화번호
  private String phone;

  // 홈페이지
  private String homepageUrl;

  // 객석수
  private Integer seatScale;

  // 확인 여부
  private boolean verified;

  // 출처 반영일시
  private LocalDateTime sourceSyncedAt;

  // 장소 생성
  public static EventVenue create(EventVenueSyncData data) {
    EventVenue entity = new EventVenue();
    entity.sourceType = data.getSourceType();
    entity.sourceId = data.getSourceId();
    entity.update(data);
    return entity;
  }

  // 장소 갱신
  public void update(EventVenueSyncData data) {
    this.name = data.getName();
    this.address = data.getAddress();
    this.sidoCode = data.getSidoCode();
    this.sigungu = data.getSigungu();
    this.latitude = data.getLatitude();
    this.longitude = data.getLongitude();
    this.phone = data.getPhone();
    this.homepageUrl = data.getHomepageUrl();
    this.seatScale = data.getSeatScale();
    this.verified = this.verified || data.getSidoCode() != null;
    this.sourceSyncedAt = LocalDateTime.now();
  }
}
