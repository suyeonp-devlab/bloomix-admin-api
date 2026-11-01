package com.bloomix.admin.domain.batch.job.cultureinfo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class CultureInfoListItem {

  private String seq;          // 일련번호
  private String serviceName;  // 서비스명
  private String realmName;    // 분류명
  private String title;        // 제목
  private String startDate;    // 시작일
  private String endDate;      // 마감일
  private String place;        // 장소
  private String area;         // 지역
  private String sigungu;      // 시군구
  private String thumbnail;    // 썸네일
  private String gpsX;         // GPS-X좌표 (경도)
  private String gpsY;         // GPS-Y좌표 (위도)
}
