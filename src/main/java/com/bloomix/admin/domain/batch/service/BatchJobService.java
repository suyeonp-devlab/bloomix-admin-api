package com.bloomix.admin.domain.batch.service;

import com.bloomix.admin.domain.batch.dto.BatchJobListRequest;
import com.bloomix.admin.domain.batch.dto.BatchJobRequest;
import com.bloomix.admin.domain.batch.dto.BatchJobResponse;
import com.bloomix.admin.domain.batch.dto.BatchJobRunRequest;
import com.bloomix.admin.domain.batch.entity.BatchJob;
import com.bloomix.admin.domain.batch.entity.BatchJobHistory;
import com.bloomix.admin.domain.batch.entity.BatchTriggerType;
import com.bloomix.admin.domain.batch.repository.BatchJobHistoryRepository;
import com.bloomix.admin.domain.batch.repository.BatchJobRepository;
import com.bloomix.admin.domain.batch.support.BatchJobLauncher;
import com.bloomix.admin.domain.batch.support.BatchJobScheduler;
import com.bloomix.admin.exception.BizException;
import com.bloomix.admin.exception.ErrorCode;
import com.bloomix.admin.response.PageResponse;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BatchJobService {

  private final BatchJobRepository batchJobRepository;
  private final BatchJobHistoryRepository batchJobHistoryRepository;
  private final BatchJobLauncher batchJobLauncher;
  private final BatchJobScheduler batchJobScheduler;

  /** 배치 목록 조회 */
  public PageResponse<BatchJobResponse> searchJobs(BatchJobListRequest condition, Pageable pageable) {

    Page<BatchJob> jobs = batchJobRepository.searchJobs(condition, pageable);

    // 최근 실행 이력
    List<String> jobIds = jobs.getContent().stream().map(BatchJob::getJobId).toList();
    Map<String, BatchJobHistory> latestHistories = batchJobHistoryRepository.findLatestByJobIds(jobIds).stream()
        .collect(Collectors.toMap(BatchJobHistory::getJobId, Function.identity()));

    return PageResponse.from(
      jobs.map(job -> BatchJobResponse.of(job, latestHistories.get(job.getJobId()))));
  }

  /** 배치 상세 조회 */
  public BatchJobResponse getJob(String jobId) {

    BatchJob job = batchJobRepository.findById(jobId)
      .orElseThrow(() -> new BizException(ErrorCode.BATCH_JOB_NOT_FOUND));

    return BatchJobResponse.of(job,
      batchJobHistoryRepository.findFirstByJobIdOrderByStartedAtDescHistoryIdDesc(job.getJobId()).orElse(null));
  }

  /** 배치 등록 */
  @Transactional
  public void createJob(BatchJobRequest request) {

    if (batchJobRepository.existsById(request.jobId())) {
      throw new BizException(ErrorCode.DUPLICATE_BATCH_JOB);
    }

    validateJob(request.jobId(), request.cronExpression());

    // DB 저장 + 스케줄 등록
    batchJobRepository.save(BatchJob.create(request));
    rescheduleAfterCommit(request.jobId());
  }

  /** 배치 수정 */
  @Transactional
  public void updateJob(String jobId, BatchJobRequest request) {

    BatchJob job = batchJobRepository.findById(jobId)
      .orElseThrow(() -> new BizException(ErrorCode.BATCH_JOB_NOT_FOUND));

    validateJob(jobId, request.cronExpression());

    // DB 수정 + 스케줄 수정
    job.update(request);
    rescheduleAfterCommit(jobId);
  }

  /** 배치 즉시 실행 */
  @Transactional(propagation = Propagation.NOT_SUPPORTED)
  public void runJob(String jobId, BatchJobRunRequest request) {
    Long historyId = batchJobLauncher.run(jobId, BatchTriggerType.MANUAL, request == null ? null : request.parameters());
    log.debug("batch execution triggered manually. jobId={}, historyId={}", jobId, historyId);
  }

  /** 배치 재시작 */
  @Transactional(propagation = Propagation.NOT_SUPPORTED)
  public void restartJob(Long historyId) {
    Long newHistoryId = batchJobLauncher.restart(historyId);
    log.debug("batch restarted. historyId={}, newHistoryId={}", historyId, newHistoryId);
  }

  /** 배치 등록 및 수정 검사 */
  private void validateJob(String jobId, String cronExpression) {

    if (!batchJobLauncher.hasJob(jobId)) {
      throw new BizException(ErrorCode.BATCH_JOB_NOT_DEFINED);
    }

    if (cronExpression != null && !CronExpression.isValidExpression(cronExpression)) {
      throw new BizException(ErrorCode.INVALID_INPUT, "실행 주기 형식이 올바르지 않습니다.");
    }
  }

  /** 스케줄 재등록 */
  private void rescheduleAfterCommit(String jobId) {
    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
      @Override
      public void afterCommit() {
        batchJobScheduler.reschedule(jobId);
      }
    });
  }
}
