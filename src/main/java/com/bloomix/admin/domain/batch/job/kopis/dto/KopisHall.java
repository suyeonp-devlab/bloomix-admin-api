package com.bloomix.admin.domain.batch.job.kopis.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class KopisHall {

  private String mt13id;             // 공연장ID
  private String prfplcnm;           // 공연장명
  private String seatscale;          // 좌석규모
  private String stageorchat;        // 무대시설-오케스트라피트
  private String stagepracat;        // 무대시설-연습실 (Y/N)
  private String stagedresat;        // 무대시설-분장실 (Y/N)
  private String stageoutdrat;       // 무대시설-야외공연장 (Y/N)
  private String disabledseatscale;  // 장애인시설-관객석 수
  private String stagearea;          // 무대시설-무대넓이
}
