package com.bloomix.admin.domain.batch.dto;

import com.bloomix.admin.domain.batch.entity.BatchStatus;
import com.bloomix.admin.domain.batch.entity.BatchJob;
import com.bloomix.admin.domain.batch.entity.BatchJobHistory;
import java.time.LocalDateTime;

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
  LocalDateTime lastStartedAt,
  LocalDateTime lastEndedAt
) {

  public static BatchJobResponse of(BatchJob job, BatchJobHistory lastHistory) {
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
        lastHistory == null ? null : lastHistory.getStartedAt(),
        lastHistory == null ? null : lastHistory.getEndedAt()
    );
  }
}
