package com.bloomix.admin.domain.batch.service;

import com.bloomix.admin.domain.batch.dto.BatchJobHistoryListRequest;
import com.bloomix.admin.domain.batch.dto.BatchJobHistoryResponse;
import com.bloomix.admin.domain.batch.entity.BatchJobHistory;
import com.bloomix.admin.domain.batch.entity.BatchStatus;
import com.bloomix.admin.domain.batch.entity.BatchTriggerType;
import com.bloomix.admin.domain.batch.repository.BatchJobHistoryRepository;
import com.bloomix.admin.exception.BizException;
import com.bloomix.admin.exception.ErrorCode;
import com.bloomix.admin.response.PageResponse;
import com.bloomix.admin.util.ExceptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BatchJobHistoryService {

  private static final String SERVER_RESTART_MESSAGE = "서버 재시작으로 중단되었습니다.";

  private final BatchJobHistoryRepository batchJobHistoryRepository;

  /** 배치 실행 이력 목록 조회 */
  public PageResponse<BatchJobHistoryResponse> searchHistories(BatchJobHistoryListRequest condition, Pageable pageable) {
    return PageResponse.from(
      batchJobHistoryRepository.searchHistories(condition, pageable));
  }

  /** 배치 실행 이력 생성 (RUNNING) */
  @Transactional
  public Long createRunning(String jobId, BatchTriggerType triggerType, Map<String, String> parameters) {
    return createRunning(jobId, triggerType, parameters, null);
  }

  /** 배치 실행 이력 생성 (RUNNING, Spring Batch 실행 ID 미리 연결) */
  @Transactional
  public Long createRunning(String jobId, BatchTriggerType triggerType, Map<String, String> parameters, Long jobExecutionId) {
    try {
      BatchJobHistory history = BatchJobHistory.start(jobId, triggerType, parameters);
      history.linkExecution(jobExecutionId);
      return batchJobHistoryRepository.saveAndFlush(history).getHistoryId();
    } catch (DataIntegrityViolationException e) {
      throw new BizException(ErrorCode.BATCH_ALREADY_RUNNING);
    }
  }

  /** Spring Batch 실행 ID 연결 */
  @Transactional
  public void linkExecution(JobExecution jobExecution) {
    findRunning(jobExecution).ifPresent(history -> history.linkExecution(jobExecution.getId()));
  }

  /** 배치 실행 종료 */
  @Transactional
  public void finish(JobExecution jobExecution) {

    findRunning(jobExecution).ifPresent(history -> {
      boolean completed = jobExecution.getStatus() == org.springframework.batch.core.BatchStatus.COMPLETED;

      // beforeJob 없이 종료된 경우 대비
      history.linkExecution(jobExecution.getId());

      history.finish(
        completed ? BatchStatus.COMPLETED : BatchStatus.FAILED,
        completed ? null : failureMessage(jobExecution));
    });
  }

  /** 배치 실행 종료 (이력 ID 기준) */
  @Transactional
  public void finish(Long historyId, BatchStatus status, String message) {
    finish(historyId, status, message, null);
  }

  /** 배치 실행 종료 (이력 ID 기준, Spring Batch 실행 ID 연결) */
  @Transactional
  public void finish(Long historyId, BatchStatus status, String message, Long jobExecutionId) {
    batchJobHistoryRepository.findById(historyId).ifPresent(history -> {
      if (jobExecutionId != null) history.linkExecution(jobExecutionId);
      history.finish(status, message);
    });
  }

  /** (앱 시작 시) 남아 있는 RUNNING 이력 정리 */
  @Transactional
  public void failStaleRunning() {

    List<BatchJobHistory> running = batchJobHistoryRepository.findByStatus(BatchStatus.RUNNING);
    running.forEach(history -> history.finish(BatchStatus.FAILED, SERVER_RESTART_MESSAGE));

    if (!running.isEmpty()) {
      log.warn("RUNNING 상태로 남은 배치 이력 {}건을 FAILED로 변경", running.size());
    }
  }

  /** 배치 RUNNING 이력 조회 */
  private Optional<BatchJobHistory> findRunning(JobExecution jobExecution) {

    String jobId = jobExecution.getJobInstance().getJobName();
    Optional<BatchJobHistory> history = batchJobHistoryRepository.findByJobIdAndStatus(jobId, BatchStatus.RUNNING);

    if (history.isEmpty()) {
      log.warn("실행 이력이 없는 배치 실행입니다. jobId={}, executionId={}", jobId, jobExecution.getId());
    }

    return history;
  }

  /** 배치 실패 원인 메시지 추출 */
  private String failureMessage(JobExecution jobExecution) {

    // Step 예외
    for (StepExecution step : jobExecution.getStepExecutions()) {
      if (!step.getFailureExceptions().isEmpty()) {
        return ExceptionUtil.summary(step.getFailureExceptions().getFirst());
      }
    }

    // Job 예외
    if (!jobExecution.getFailureExceptions().isEmpty()) {
      return ExceptionUtil.summary(jobExecution.getFailureExceptions().getFirst());
    }

    return jobExecution.getExitStatus().getExitDescription();
  }
}
