package com.bloomix.admin.domain.batch.job.holiday.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class HolidayItem {

  private String locdate;     // 날짜
  private String dateName;    // 공휴일명
  private String isHoliday;   // 공휴일 여부
}
