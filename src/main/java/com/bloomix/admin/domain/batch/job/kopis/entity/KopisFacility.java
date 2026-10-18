package com.bloomix.admin.domain.batch.job.kopis.entity;

import static com.bloomix.admin.domain.batch.job.kopis.KopisValueParser.decimal;
import static com.bloomix.admin.domain.batch.job.kopis.KopisValueParser.integer;
import static com.bloomix.admin.domain.batch.job.kopis.KopisValueParser.text;
import static com.bloomix.admin.domain.batch.job.kopis.KopisValueParser.yn;

import com.bloomix.admin.domain.BaseEntity;
import com.bloomix.admin.domain.batch.job.kopis.dto.KopisFacilityDetail;
import com.bloomix.admin.domain.batch.job.kopis.dto.KopisHall;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KopisFacility extends BaseEntity {

  @Id
  private String facilityId;

  // 공연시설명
  private String name;

  // 공연장 수
  private Integer hallCount;

  // 시설특성
  private String characteristic;

  // 개관연도
  private String openedYear;

  // 객석수
  private Integer seatScale;

  // 전화번호
  private String phone;

  // 홈페이지
  private String homepageUrl;

  // 주소
  private String address;

  // 위도
  private BigDecimal latitude;

  // 경도
  private BigDecimal longitude;

  // 레스토랑
  private boolean restaurant;

  // 카페
  private boolean cafe;

  // 편의점
  private boolean store;

  // 놀이방
  private boolean kidsRoom;

  // 수유실
  private boolean nursingRoom;

  // 주차시설
  private boolean parkingLot;

  // 장애시설-주차장
  private boolean barrierParking;

  // 장애시설-화장실
  private boolean barrierRestroom;

  // 장애시설-경사로
  private boolean barrierRamp;

  // 장애시설-엘리베이터
  private boolean barrierElevator;

  // 공연장 목록
  @JdbcTypeCode(SqlTypes.JSON)
  private List<KopisHall> halls;

  // 시설 생성
  public static KopisFacility create(KopisFacilityDetail detail) {
    KopisFacility entity = new KopisFacility();
    entity.facilityId = text(detail.mt10id());
    entity.update(detail);
    return entity;
  }

  // 시설 갱신
  public void update(KopisFacilityDetail detail) {
    this.name = text(detail.fcltynm());
    this.hallCount = integer(detail.mt13cnt());
    this.characteristic = text(detail.fcltychartr());
    this.openedYear = text(detail.opende());
    this.seatScale = integer(detail.seatscale());
    this.phone = text(detail.telno());
    this.homepageUrl = text(detail.relateurl());
    this.address = text(detail.adres());
    this.latitude = decimal(detail.la());
    this.longitude = decimal(detail.lo());
    this.restaurant = yn(detail.restaurant());
    this.cafe = yn(detail.cafe());
    this.store = yn(detail.store());
    this.kidsRoom = yn(detail.nolibang());
    this.nursingRoom = yn(detail.suyu());
    this.parkingLot = yn(detail.parkinglot());
    this.barrierParking = yn(detail.parkbarrier());
    this.barrierRestroom = yn(detail.restbarrier());
    this.barrierRamp = yn(detail.runwbarrier());
    this.barrierElevator = yn(detail.elevbarrier());
    this.halls = detail.mt13s();
  }
}
