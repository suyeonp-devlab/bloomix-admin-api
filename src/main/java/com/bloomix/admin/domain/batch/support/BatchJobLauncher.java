package com.bloomix.admin.domain.batch.support;

import com.bloomix.admin.domain.batch.entity.BatchStatus;
import com.bloomix.admin.domain.batch.entity.BatchJobHistory;
import com.bloomix.admin.domain.batch.entity.BatchTriggerType;
import com.bloomix.admin.domain.batch.repository.BatchJobHistoryRepository;
import com.bloomix.admin.domain.batch.repository.BatchJobRepository;
import com.bloomix.admin.domain.batch.service.BatchJobHistoryService;
import com.bloomix.admin.exception.BizException;
import com.bloomix.admin.exception.ErrorCode;
import java.util.Map;

import com.bloomix.admin.util.ExceptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.stereotype.Component;

/** 배치 실행기 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BatchJobLauncher {

  private static final String RUN_ID = "runId";

  private final JobOperator jobOperator;
  private final JobRegistry jobRegistry;
  private final JobRepository jobRepository;
  private final BatchJobRepository batchJobRepository;
  private final BatchJobHistoryRepository batchJobHistoryRepository;
  private final BatchJobHistoryService batchJobHistoryService;

  /** Job 존재 여부 */
  public boolean hasJob(String jobId) {
    return jobRegistry.getJobNames().contains(jobId);
  }

  /** 배치 실행 */
  public Long run(String jobId, BatchTriggerType triggerType, Map<String, String> parameters) {

    if (!batchJobRepository.existsById(jobId)) {
      throw new BizException(ErrorCode.BATCH_JOB_NOT_FOUND);
    }

    if (!hasJob(jobId)) {
      throw new BizException(ErrorCode.BATCH_JOB_NOT_DEFINED);
    }

    Job job = jobRegistry.getJob(jobId);

    // 실행 이력 생성 (RUNNING)
    Map<String, String> params = parameters == null ? Map.of() : parameters;
    Long historyId = batchJobHistoryService.createRunning(jobId, triggerType, params);

    // Job 파라미터
    JobParametersBuilder builder = new JobParametersBuilder();
    params.forEach(builder::addString);
    builder.addLong(RUN_ID, historyId);

    // Job 시작
    JobExecution execution = null;
    try {
      execution = jobOperator.start(job, builder.toJobParameters());
    } catch (Exception e) {
      launchFailed(historyId, jobId, e);
    }

    checkLaunched(historyId, jobId, execution);
    return historyId;
  }

  /** 배치 재시작 (실패 지점부터) */
  public Long restart(Long historyId) {

    BatchJobHistory failed = batchJobHistoryRepository.findById(historyId)
        .orElseThrow(() -> new BizException(ErrorCode.BATCH_HISTORY_NOT_FOUND));

    validateRestart(failed);

    JobExecution failedExecution = jobRepository.getJobExecution(failed.getJobExecutionId());
    if (failedExecution == null) {
      throw new BizException(ErrorCode.BATCH_RESTART_NOT_ALLOWED);
    }

    // 실행 이력 생성 (RUNNING)
    // 실패한 실행 ID 미리 연결: 재시작이 시작 단계에서 실패해도 다시 재시작할 수 있도록
    Long newHistoryId = batchJobHistoryService.createRunning(
        failed.getJobId(), BatchTriggerType.RESTART, failed.getParameters(), failedExecution.getId());

    // Job 재시작
    JobExecution execution = null;
    try {
      execution = jobOperator.restart(failedExecution);
    } catch (Exception e) {
      launchFailed(newHistoryId, failed.getJobId(), e);
    }

    checkLaunched(newHistoryId, failed.getJobId(), execution);
    return newHistoryId;
  }

  /** 배치 재시작 가능 여부 검사 */
  private void validateRestart(BatchJobHistory history) {

    boolean latest = batchJobHistoryRepository.findFirstByJobIdOrderByStartedAtDescHistoryIdDesc(history.getJobId())
        .map(last -> last.getHistoryId().equals(history.getHistoryId()))
        .orElse(false);

    // 실패이력 + 실행 기록 존재 + 최근 실행
    if (history.getStatus() != BatchStatus.FAILED || history.getJobExecutionId() == null || !latest) {
      throw new BizException(ErrorCode.BATCH_RESTART_NOT_ALLOWED);
    }
  }

  /** Job 시작 실패 처리 */
  private void launchFailed(Long historyId, String jobId, Exception e) {
    log.error("배치 실행 시작 실패. jobId={}, historyId={}", jobId, historyId, e);
    batchJobHistoryService.finish(historyId, BatchStatus.FAILED, ExceptionUtil.summary(e));
    throw new BizException(ErrorCode.BATCH_LAUNCH_FAILED);
  }

  /**
   * Job 시작 여부 확인
   * 스레드풀 포화 시 예외 없이 FAILED 리턴 → 이력이 RUNNING으로 남는 것 방지
   */
  private void checkLaunched(Long historyId, String jobId, JobExecution execution) {

    if (execution.getStatus() != org.springframework.batch.core.BatchStatus.FAILED
      || execution.getStartTime() != null) {
      return;
    }

    String description = execution.getExitStatus().getExitDescription();
    String message = description.lines().findFirst().orElse(null);

    log.error("배치 실행 시작 실패 (스레드풀 거부). jobId={}, historyId={}, executionId={}", jobId, historyId, execution.getId());
    batchJobHistoryService.finish(historyId, BatchStatus.FAILED, message, execution.getId());
    throw new BizException(ErrorCode.BATCH_LAUNCH_FAILED);
  }
}
