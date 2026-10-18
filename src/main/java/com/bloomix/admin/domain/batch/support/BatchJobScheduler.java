package com.bloomix.admin.domain.batch.support;

import com.bloomix.admin.domain.batch.entity.BatchJob;
import com.bloomix.admin.domain.batch.entity.BatchTriggerType;
import com.bloomix.admin.domain.batch.repository.BatchJobRepository;
import com.bloomix.admin.domain.batch.service.BatchJobHistoryService;
import com.bloomix.admin.exception.BizException;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

/**
 * 배치 스케줄러: batch_job 테이블의 cron으로 자동 실행 등록
 * 배치 등록 및 수정 시 재등록
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BatchJobScheduler {

  private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");

  private final ThreadPoolTaskScheduler batchTaskScheduler;
  private final BatchJobRepository batchJobRepository;
  private final BatchJobHistoryService batchJobHistoryService;
  private final BatchJobLauncher batchJobLauncher;

  // 등록된 예약
  private final Map<String, ScheduledFuture<?>> scheduled = new ConcurrentHashMap<>();

  /** 서버 기동 시 초기화 */
  @EventListener(ApplicationReadyEvent.class)
  public void initialize() {

    // 남은 RUNNING 이력 정리
    batchJobHistoryService.failStaleRunning();

    // 자동 실행 등록
    List<BatchJob> jobs = batchJobRepository.findByEnabledTrueAndCronExpressionIsNotNull();
    for (BatchJob job : jobs) register(job);

    log.info("배치 자동 실행 {}건 등록", scheduled.size());
  }

  /** 자동 실행 재등록 */
  public void reschedule(String jobId) {

    cancel(jobId);
    batchJobRepository.findById(jobId)
        .filter(batchJob -> batchJob.isEnabled() && batchJob.getCronExpression() != null)
        .ifPresent(this::register);
  }

  /** 자동 실행 등록 */
  private void register(BatchJob batchJob) {

    String jobId = batchJob.getJobId();

    if (!batchJobLauncher.hasJob(jobId)) {
      log.warn("(자동 실행 미등록) 코드에 정의되지 않은 배치: jobId={}", jobId);
      return;
    }

    try {
      ScheduledFuture<?> future = batchTaskScheduler.schedule(
          () -> runScheduled(jobId),
          new CronTrigger(batchJob.getCronExpression(), ZONE));

      scheduled.put(jobId, future);
    } catch (IllegalArgumentException e) {
      log.error("(자동 실행 미등록) cron 형식 오류: jobId={}, cron={}", jobId, batchJob.getCronExpression());
    }
  }

  /** 자동 실행 해제 */
  private void cancel(String jobId) {
    ScheduledFuture<?> future = scheduled.remove(jobId);
    if (future != null) future.cancel(false);
  }

  /** 자동 실행 */
  private void runScheduled(String jobId) {
    try {
      batchJobLauncher.run(jobId, BatchTriggerType.SCHEDULED, Map.of());
    } catch (BizException e) {
      log.warn("배치 자동 실행 건너뜀. jobId={}, reason={}", jobId, e.getMessage());
    } catch (Exception e) {
      log.error("배치 자동 실행 실패. jobId={}", jobId, e);
    }
  }
}
