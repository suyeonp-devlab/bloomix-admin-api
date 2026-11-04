package com.bloomix.admin.domain.event.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public record EventScheduleRequest(
  @NotNull(message = "회차 날짜를 입력해주세요.")
  LocalDate scheduleDate,

  LocalTime startTime,  // 시작 시간 (종일 회차는 비움)

  @NotNull(message = "정원을 입력해주세요.")
  @Min(value = 1, message = "정원은 1명 이상으로 입력해주세요.")
  Integer capacity,

  @NotNull(message = "사용 여부를 입력해주세요.")
  Boolean enabled
) {
}
