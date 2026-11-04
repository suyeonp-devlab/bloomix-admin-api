package com.bloomix.admin.domain.event.dto;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public record EventScheduleListRequest(
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate scheduleFrom,
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate scheduleTo,
  Boolean enabled
) {
}
