package com.bloomix.admin.domain.batch.job.kopis.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KopisPerformanceListItem(
  String mt20id,     // 공연ID
  String prfnm,      // 공연명
  String prfpdfrom,  // 공연시작일
  String prfpdto,    // 공연종료일
  String fcltynm,    // 공연시설명
  String poster,     // 포스터 이미지 경로
  String area,       // 공연지역
  String genrenm,    // 공연 장르명
  String openrun,    // 오픈런 (Y/N)
  String prfstate    // 공연상태
) {
}
