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
  Map<String, String> parameters,
  BatchStatus status,
  LocalDateTime startedAt,
  LocalDateTime endedAt,
  String message,
  String executedBy
) {

  public static BatchJobHistoryResponse of(BatchJobHistory history, String jobName) {
    return new BatchJobHistoryResponse(
        history.getHistoryId(),
        history.getJobId(),
        jobName,
        history.getTriggerType(),
        history.getParameters(),
        history.getStatus(),
        history.getStartedAt(),
        history.getEndedAt(),
        history.getMessage(),
        history.getCreatedBy()
    );
  }
}
