package com.bloomix.admin.domain.batch.support;

import com.bloomix.admin.domain.batch.service.BatchJobHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.stereotype.Component;

/**
 * 배치 실행 이력 리스너: Job 시작·종료 시 이력 기록
 * 모든 Job에 자동 적용 (BatchConfig)
 */
@Component
@RequiredArgsConstructor
public class BatchJobHistoryListener implements JobExecutionListener {

  private final BatchJobHistoryService batchJobHistoryService;

  @Override
  public void beforeJob(JobExecution jobExecution) {
    batchJobHistoryService.linkExecution(jobExecution);
  }

  @Override
  public void afterJob(JobExecution jobExecution) {
    batchJobHistoryService.finish(jobExecution);
  }
}
