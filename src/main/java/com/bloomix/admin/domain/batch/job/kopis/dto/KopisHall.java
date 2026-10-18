package com.bloomix.admin.domain.batch.job.kopis.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KopisHall(
  String mt13id,             // 공연장ID
  String prfplcnm,           // 공연장명
  String seatscale,          // 좌석규모
  String stageorchat,        // 무대시설-오케스트라피트
  String stagepracat,        // 무대시설-연습실 (Y/N)
  String stagedresat,        // 무대시설-분장실 (Y/N)
  String stageoutdrat,       // 무대시설-야외공연장 (Y/N)
  String disabledseatscale,  // 장애인시설-관객석 수
  String stagearea           // 무대시설-무대넓이
) {
}
