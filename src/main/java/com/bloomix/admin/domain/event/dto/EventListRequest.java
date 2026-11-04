package com.bloomix.admin.domain.event.dto;

import com.bloomix.admin.domain.event.entity.EventCategory;
import com.bloomix.admin.domain.event.entity.EventSourceType;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public record EventListRequest(
  String searchType,
  String keyword,
  EventCategory category,
  EventSourceType sourceType,
  String sidoCode,
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodFrom,
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodTo,
  Boolean enabled,
  Boolean verified,
  Boolean onSale,
  Boolean hasSchedule,
  Boolean hasSourceChange
) {
}
