package com.bloomix.admin.domain.batch.dto;

import com.bloomix.admin.domain.batch.entity.BatchStatus;
import com.bloomix.admin.domain.batch.entity.BatchTriggerType;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public record BatchJobHistoryListRequest(
  String searchType,
  String keyword,
  BatchStatus status,
  BatchTriggerType triggerType,
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startedFrom,
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startedTo
) {
}
