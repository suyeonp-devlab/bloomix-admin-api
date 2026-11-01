package com.bloomix.admin.domain.batch.job.cultureinfo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class CultureInfoDetail {

  private String seq;         // 일련번호
  private String title;       // 제목
  private String startDate;   // 시작일
  private String endDate;     // 마감일
  private String place;       // 장소
  private String realmName;   // 분류명
  private String area;        // 지역
  private String sigungu;     // 시군구
  private String price;       // 티켓요금
  private String contents1;   // 내용
  private String url;         // 관람URL
  private String phone;       // 문의처
  private String gpsX;        // GPS-X좌표 (경도)
  private String gpsY;        // GPS-Y좌표 (위도)
  private String imgUrl;      // 이미지
  private String placeUrl;    // 공연장URL
  private String placeAddr;   // 공연장주소
  private String placeSeq;    // 문화예술공간 일련번호
}
