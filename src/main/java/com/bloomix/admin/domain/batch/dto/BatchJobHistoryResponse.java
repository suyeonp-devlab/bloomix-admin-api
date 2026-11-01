package com.bloomix.admin.domain.batch.dto;

import com.bloomix.admin.domain.batch.entity.BatchStatus;
import com.bloomix.admin.domain.batch.entity.BatchJobHistory;
import com.bloomix.admin.domain.batch.entity.BatchTriggerType;
import java.time.LocalDateTime;
import java.util.Map;

public record BatchJobHistoryResponse(
  Long historyId,
  String jobId,
  String jobName,
  BatchTriggerType triggerType,
  String triggerTypeName,
  Map<String, String> parameters,
  BatchStatus status,
  String statusName,
  LocalDateTime startedAt,
  LocalDateTime endedAt,
  String message,
  String executedBy
) {

  public static BatchJobHistoryResponse of(
      BatchJobHistory history,
      String jobName,
      Map<String, String> triggerTypeNames,
      Map<String, String> statusNames
  ) {
    return new BatchJobHistoryResponse(
        history.getHistoryId(),
        history.getJobId(),
        jobName,
        history.getTriggerType(),
        triggerTypeNames.get(history.getTriggerType().name()),
        history.getParameters(),
        history.getStatus(),
        statusNames.get(history.getStatus().name()),
        history.getStartedAt(),
        history.getEndedAt(),
        history.getMessage(),
        history.getCreatedBy()
    );
  }
}
