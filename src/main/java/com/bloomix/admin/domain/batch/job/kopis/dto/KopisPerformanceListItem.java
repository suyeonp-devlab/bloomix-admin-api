package com.bloomix.admin.domain.batch.job.kopis.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class KopisPerformanceListItem {

  private String mt20id;     // 공연ID
  private String prfnm;      // 공연명
  private String prfpdfrom;  // 공연시작일
  private String prfpdto;    // 공연종료일
  private String fcltynm;    // 공연시설명
  private String poster;     // 포스터 이미지 경로
  private String area;       // 공연지역
  private String genrenm;    // 공연 장르명
  private String openrun;    // 오픈런 (Y/N)
  private String prfstate;   // 공연상태
}
