package com.bloomix.admin.domain.batch.job.holiday;

import com.bloomix.admin.domain.batch.job.holiday.client.HolidayClient;
import com.bloomix.admin.domain.batch.job.holiday.dto.HolidayItem;
import com.bloomix.admin.domain.holiday.entity.Holiday;
import com.bloomix.admin.domain.holiday.repository.HolidayRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.bloomix.admin.util.DateUtil.YYYYMMDD;

/**
 * 한국천문연구원 특일 정보 수집 배치
 * Step 1 공휴일 수집
 */
@Slf4j
@Configuration
public class HolidayCollectJobConfig {

  public static final String JOB_NAME = "HOLIDAY_COLLECT";

  // 조회 재시도
  private static final int FETCH_ATTEMPTS = 3;
  private static final long FETCH_RETRY_DELAY_MILLIS = 1000;

  @Bean
  public Job holidayCollectJob(JobRepository jobRepository, Step holidayCollectStep) {
    return new JobBuilder(JOB_NAME, jobRepository)
        .start(holidayCollectStep)
        .build();
  }

  /** [Step 1] 공휴일 수집 */
  @Bean
  public Step holidayCollectStep(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      Tasklet holidayCollectTasklet
  ) {
    return new StepBuilder("holidayCollectStep", jobRepository)
        .tasklet(holidayCollectTasklet, transactionManager)
        .build();
  }

  /** 공휴일 수집 Tasklet */
  @Bean
  public Tasklet holidayCollectTasklet(HolidayClient holidayClient, HolidayRepository holidayRepository) {

    return (contribution, chunkContext) -> {
      int thisYear = LocalDate.now().getYear();
      for (int year = thisYear; year <= thisYear + 1; year++) {
        int saved = saveHolidays(holidayRepository, year, fetch(holidayClient, year));
        contribution.incrementWriteCount(saved);
      }
      return RepeatStatus.FINISHED;
    };
  }

  /** 연도별 공휴일 저장 */
  private static int saveHolidays(
      HolidayRepository holidayRepository,
      int year,
      List<HolidayItem> items
  ) {

    // 공휴일이 동일 날짜인 경우 이름을 합침 (예: 어린이날, 부처님오신날)
    Map<LocalDate, String> holidays = items.stream()
        .filter(item -> "Y".equals(item.getIsHoliday()))
        .collect(Collectors.toMap(
            item -> LocalDate.parse(item.getLocdate(), YYYYMMDD),
            HolidayItem::getDateName,
            (first, second) -> first + ", " + second
        ));

    if (holidays.isEmpty()) return 0;

    LocalDate from = LocalDate.of(year, 1, 1);
    LocalDate to = LocalDate.of(year, 12, 31);

    Map<LocalDate, Holiday> existing = holidayRepository
        .findAllByHolidayDateBetween(from, to).stream()
        .collect(Collectors.toMap(Holiday::getHolidayDate, Function.identity()));

    // 응답에서 빠진 날짜 삭제
    holidayRepository.deleteAll(existing.values().stream()
        .filter(holiday -> !holidays.containsKey(holiday.getHolidayDate()))
        .toList());

    // 기존은 갱신(변경 감지), 신규는 추가
    List<Holiday> created = new ArrayList<>();
    holidays.forEach((date, name) -> {
      Holiday holiday = existing.get(date);
      if (holiday != null) holiday.update(name);
      else created.add(Holiday.create(date, name));
    });

    holidayRepository.saveAll(created);
    return holidays.size();
  }

  /** 공휴일 조회 */
  private static List<HolidayItem> fetch(HolidayClient holidayClient, int year) {

    for (int attempt = 1; ; attempt++) {
      try {
        return holidayClient.getHolidays(year);
      } catch (HolidayApiException e) {
        if (attempt >= FETCH_ATTEMPTS) throw e;
        log.warn("공휴일 조회 재시도 {}/{}: {}", attempt, FETCH_ATTEMPTS, e.getMessage());
        sleep();
      }
    }
  }

  /** 공휴일 조회 재시도 전 대기 */
  private static void sleep() {
    try {
      Thread.sleep(FETCH_RETRY_DELAY_MILLIS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("한국천문연구원 공휴일 조회 중단", e);
    }
  }
}
