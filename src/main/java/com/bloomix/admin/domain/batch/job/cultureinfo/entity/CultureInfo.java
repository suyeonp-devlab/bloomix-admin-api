package com.bloomix.admin.domain.batch.job.cultureinfo.entity;

import static com.bloomix.admin.domain.batch.job.cultureinfo.CultureInfoValueParser.date;
import static com.bloomix.admin.domain.batch.job.cultureinfo.CultureInfoValueParser.decimal;
import static com.bloomix.admin.domain.batch.job.cultureinfo.CultureInfoValueParser.text;

import com.bloomix.admin.domain.BaseEntity;
import com.bloomix.admin.domain.batch.job.cultureinfo.dto.CultureInfoDetail;
import com.bloomix.admin.domain.batch.job.cultureinfo.dto.CultureInfoListItem;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CultureInfo extends BaseEntity {

  @Id
  private String seq;

  // 서비스구분
  private String serviceType;

  // 서비스명
  private String serviceName;

  // 분류명
  private String realmName;

  // 제목
  private String title;

  // 시작일
  private LocalDate startDate;

  // 마감일
  private LocalDate endDate;

  // 장소
  private String place;

  // 지역
  private String area;

  // 시군구
  private String sigungu;

  // 썸네일
  private String thumbnailUrl;

  // 경도
  private BigDecimal longitude;

  // 위도
  private BigDecimal latitude;

  // 티켓요금
  private String priceText;

  // 내용
  private String contents;

  // 관람URL
  private String viewUrl;

  // 문의처
  private String phone;

  // 이미지
  private String imageUrl;

  // 공연장URL
  private String placeUrl;

  // 공연장주소
  private String placeAddress;

  // 문화예술공간 일련번호
  private String placeSeq;

  // 상세 수집일시
  private LocalDateTime detailSyncedAt;

  // 문화정보 생성
  public static CultureInfo create(
      String serviceType,
      CultureInfoListItem item,
      CultureInfoDetail detail
  ) {
    CultureInfo entity = new CultureInfo();
    entity.seq = text(item.getSeq());
    entity.serviceType = serviceType;
    entity.serviceName = text(item.getServiceName());
    entity.thumbnailUrl = text(item.getThumbnail());
    entity.update(detail);
    return entity;
  }

  // 문화정보 갱신
  public void update(CultureInfoDetail detail) {
    this.realmName = text(detail.getRealmName());
    this.title = text(detail.getTitle());
    this.startDate = date(detail.getStartDate());
    this.endDate = date(detail.getEndDate());
    this.place = text(detail.getPlace());
    this.area = text(detail.getArea());
    this.sigungu = text(detail.getSigungu());
    this.longitude = decimal(detail.getGpsX());
    this.latitude = decimal(detail.getGpsY());
    this.priceText = text(detail.getPrice());
    this.contents = text(detail.getContents1());
    this.viewUrl = text(detail.getUrl());
    this.phone = text(detail.getPhone());
    this.imageUrl = text(detail.getImgUrl());
    this.placeUrl = text(detail.getPlaceUrl());
    this.placeAddress = text(detail.getPlaceAddr());
    this.placeSeq = text(detail.getPlaceSeq());
    this.detailSyncedAt = LocalDateTime.now();
  }
}
