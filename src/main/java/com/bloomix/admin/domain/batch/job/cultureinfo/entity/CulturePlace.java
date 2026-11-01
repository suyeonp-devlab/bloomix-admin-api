package com.bloomix.admin.domain.batch.job.cultureinfo.entity;

import static com.bloomix.admin.domain.batch.job.cultureinfo.CultureInfoValueParser.decimal;
import static com.bloomix.admin.domain.batch.job.cultureinfo.CultureInfoValueParser.text;

import com.bloomix.admin.domain.BaseEntity;
import com.bloomix.admin.domain.batch.job.cultureinfo.dto.CulturePlaceDetail;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CulturePlace extends BaseEntity {

  @Id
  private String placeSeq;

  // 문화예술공간명
  private String name;

  // 문화예술공간분류명
  private String className;

  // 문화예술공간구분명
  private String groupName;

  // 전화
  private String phone;

  // 홈페이지
  private String homepageUrl;

  // 주소
  private String address;

  // 우편번호
  private String zipCode;

  // 개관일
  private String openedDate;

  // 대표자명
  private String representative;

  // 문화예술공간 소개
  private String description;

  // 전경/구성원 이미지
  private String imageUrl;

  // 경도
  private BigDecimal longitude;

  // 위도
  private BigDecimal latitude;

  // 문화예술공간 생성
  public static CulturePlace create(CulturePlaceDetail detail) {
    CulturePlace entity = new CulturePlace();
    entity.placeSeq = text(detail.getSeq());
    entity.update(detail);
    return entity;
  }

  // 문화예술공간 갱신
  public void update(CulturePlaceDetail detail) {
    this.name = text(detail.getCulName());
    this.className = text(detail.getCulClName());
    this.groupName = text(detail.getCulGrpName());
    this.phone = text(detail.getCulTel());
    this.homepageUrl = text(detail.getCulHomeUrl());
    this.address = text(detail.getCulAddr());
    this.zipCode = text(detail.getZipCode());
    this.openedDate = text(detail.getCulOpenDay());
    this.representative = text(detail.getCulOpenName());
    this.description = text(detail.getCulCont());
    this.imageUrl = text(detail.getCulViewImg1());
    this.longitude = decimal(detail.getGpsX());
    this.latitude = decimal(detail.getGpsY());
  }
}
