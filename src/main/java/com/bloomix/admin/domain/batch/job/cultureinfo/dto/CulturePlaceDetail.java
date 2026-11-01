package com.bloomix.admin.domain.batch.job.cultureinfo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class CulturePlaceDetail {

  private String seq;          // 일련번호
  private String culName;      // 문화예술공간명
  private String culClName;    // 문화예술공간분류명
  private String culGrpName;   // 문화예술공간구분명
  private String culTel;       // 전화
  private String culHomeUrl;   // 홈페이지
  private String culAddr;      // 주소
  private String zipCode;      // 우편번호
  private String culOpenDay;   // 개관일
  private String culOpenName;  // 대표자명
  private String culCont;      // 문화예술공간 소개
  private String culViewImg1;  // 전경/구성원 이미지 URL
  private String gpsX;         // GPS-X좌표 (경도)
  private String gpsY;         // GPS-Y좌표 (위도)
}
