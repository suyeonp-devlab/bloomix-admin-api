package com.bloomix.admin.domain.batch.job.eventschedule;

import com.bloomix.admin.domain.batch.job.kopis.entity.KopisPerformance;
import com.bloomix.admin.domain.batch.job.kopis.repository.KopisPerformanceRepository;
import com.bloomix.admin.domain.event.entity.Event;
import com.bloomix.admin.domain.event.entity.EventCategory;
import com.bloomix.admin.domain.event.entity.EventSchedule;
import com.bloomix.admin.domain.event.repository.EventRepository;
import com.bloomix.admin.domain.event.repository.EventScheduleRepository;
import com.bloomix.admin.domain.holiday.entity.Holiday;
import com.bloomix.admin.domain.holiday.repository.HolidayRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 회차 생성 배치
 * Step 1 오늘부터 30일 뒤까지 없는 회차 추가
 */
@Slf4j
@Configuration
public class EventScheduleGenerateJobConfig {

  public static final String JOB_NAME = "EVENT_SCHEDULE_GENERATE";

  // 커밋 단위
  private static final int CHUNK_SIZE = 100;

  // 생성 기간
  private static final int GENERATE_DAYS = 30;

  @Bean
  public Job eventScheduleGenerateJob(JobRepository jobRepository, Step eventScheduleGenerateStep) {
    return new JobBuilder(JOB_NAME, jobRepository)
        .start(eventScheduleGenerateStep)
        .build();
  }

  /** [Step 1] 회차 생성 */
  @Bean
  public Step eventScheduleGenerateStep(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      ListItemReader<Long> eventScheduleGenerateReader,
      ItemWriter<Long> eventScheduleGenerateWriter
  ) {
    return new StepBuilder("eventScheduleGenerateStep", jobRepository)
        .<Long, Long>chunk(CHUNK_SIZE)
        .transactionManager(transactionManager)
        .reader(eventScheduleGenerateReader)
        .writer(eventScheduleGenerateWriter)
        .build();
  }

  /** 회차 생성 Reader */
  @Bean
  @StepScope
  public ListItemReader<Long> eventScheduleGenerateReader(EventRepository eventRepository) {
    LocalDate today = LocalDate.now();
    return new ListItemReader<>(eventRepository.findEventIdsToGenerateSchedule(today, today.plusDays(GENERATE_DAYS)));
  }

  /** 회차 생성 Writer */
  @Bean
  public ItemWriter<Long> eventScheduleGenerateWriter(
      EventRepository eventRepository,
      EventScheduleRepository eventScheduleRepository,
      KopisPerformanceRepository kopisPerformanceRepository,
      HolidayRepository holidayRepository
  ) {
    return chunk -> {
      List<Long> ids = Collections.unmodifiableList(chunk.getItems());
      List<Event> events = eventRepository.findAllById(ids);

      LocalDate from = LocalDate.now();
      LocalDate to = from.plusDays(GENERATE_DAYS);

      List<String> performanceIds = events.stream()
          .filter(event -> event.getCategory() == EventCategory.PERFORMANCE)
          .map(Event::getSourceId)
          .toList();

      // KOPIS 공연 조회
      Map<String, KopisPerformance> performances = kopisPerformanceRepository.findAllById(performanceIds).stream()
          .collect(Collectors.toMap(KopisPerformance::getPerformanceId, Function.identity()));

      // 공휴일 조회
      Set<LocalDate> holidays = holidayRepository.findAllByHolidayDateBetween(from, to).stream()
          .map(Holiday::getHolidayDate)
          .collect(Collectors.toSet());

      // 기존 회차 조회
      Set<String> existing = eventScheduleRepository.findAllByEventIdInAndScheduleDateBetween(ids, from, to).stream()
          .map(EventScheduleGenerateJobConfig::slotKey)
          .collect(Collectors.toSet());

      // 회차 후보 생성
      List<EventSchedule> schedules = new ArrayList<>();
      for (Event event : events) {
        LocalDate start = event.getStartDate().isAfter(from) ? event.getStartDate() : from;
        LocalDate end = event.getEndDate().isBefore(to) ? event.getEndDate() : to;

        if (event.getCategory() == EventCategory.PERFORMANCE) {
          schedules.addAll(performanceSchedules(event, performances.get(event.getSourceId()), start, end, holidays));
        } else {
          schedules.addAll(allDaySchedules(event, start, end));
        }
      }

      // 없는 회차만 저장
      eventScheduleRepository.saveAll(schedules.stream()
          .filter(schedule -> !existing.contains(slotKey(schedule)))
          .toList());
    };
  }

  /** 공연 회차 생성 */
  private static List<EventSchedule> performanceSchedules(
      Event event,
      KopisPerformance performance,
      LocalDate start,
      LocalDate end,
      Set<LocalDate> holidays
  ) {

    try {
      return EventScheduleParser.parse(performance.getScheduleText(), start, end, holidays).stream()
          .map(slot -> EventSchedule.create(event, slot.toLocalDate(), slot.toLocalTime()))
          .toList();
    } catch (IllegalArgumentException e) {
      log.warn("[{}] 회차 미생성. eventId={}: {}", JOB_NAME, event.getEventId(), e.getMessage());
      return List.of();
    }
  }

  /** 전시·축제·체험 회차 생성 (날짜별 종일 1회차) */
  private static List<EventSchedule> allDaySchedules(Event event, LocalDate start, LocalDate end) {
    return start.datesUntil(end.plusDays(1))
        .map(date -> EventSchedule.create(event, date, null))
        .toList();
  }

  /** 회차 식별값 */
  private static String slotKey(EventSchedule schedule) {
    // 행사 + 날짜 + 시작 시간
    return schedule.getEventId() + "|" + schedule.getScheduleDate() + "|" + schedule.getStartTime();
  }
}
