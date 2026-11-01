package com.bloomix.admin.domain.event.entity;

import com.bloomix.admin.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventVenueHistory extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long historyId;

  // 장소ID
  private Long venueId;

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

  // 확인 여부
  private boolean verified;

  // 이력 생성
  public static EventVenueHistory create(EventVenue venue) {
    EventVenueHistory entity = new EventVenueHistory();
    entity.venueId = venue.getVenueId();
    entity.name = venue.getName();
    entity.address = venue.getAddress();
    entity.sidoCode = venue.getSidoCode();
    entity.sigungu = venue.getSigungu();
    entity.latitude = venue.getLatitude();
    entity.longitude = venue.getLongitude();
    entity.verified = venue.isVerified();
    return entity;
  }
}
