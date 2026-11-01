package com.bloomix.admin.domain.batch.dto;

import com.bloomix.admin.domain.batch.entity.BatchStatus;
import com.bloomix.admin.domain.batch.entity.BatchJob;
import com.bloomix.admin.domain.batch.entity.BatchJobHistory;
import java.time.LocalDateTime;
import java.util.Map;

public record BatchJobResponse(
  String jobId,
  String jobName,
  String description,
  String cronExpression,
  boolean enabled,
  LocalDateTime createdAt,
  LocalDateTime updatedAt,
  Long lastHistoryId,
  BatchStatus lastStatus,
  String lastStatusName,
  LocalDateTime lastStartedAt,
  LocalDateTime lastEndedAt
) {

  public static BatchJobResponse of(
      BatchJob job,
      BatchJobHistory lastHistory,
      Map<String, String> statusNames
  ) {
    return new BatchJobResponse(
        job.getJobId(),
        job.getJobName(),
        job.getDescription(),
        job.getCronExpression(),
        job.isEnabled(),
        job.getCreatedAt(),
        job.getUpdatedAt(),
        lastHistory == null ? null : lastHistory.getHistoryId(),
        lastHistory == null ? null : lastHistory.getStatus(),
        lastHistory == null ? null : statusNames.get(lastHistory.getStatus().name()),
        lastHistory == null ? null : lastHistory.getStartedAt(),
        lastHistory == null ? null : lastHistory.getEndedAt()
    );
  }
}
