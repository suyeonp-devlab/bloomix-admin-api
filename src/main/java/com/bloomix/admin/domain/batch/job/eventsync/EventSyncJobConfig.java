package com.bloomix.admin.domain.batch.job.eventsync;

import com.bloomix.admin.domain.batch.job.cultureinfo.entity.CultureInfo;
import com.bloomix.admin.domain.batch.job.cultureinfo.repository.CultureInfoRepository;
import com.bloomix.admin.domain.batch.job.cultureinfo.repository.CulturePlaceRepository;
import com.bloomix.admin.domain.batch.job.eventsync.dto.EventSyncData;
import com.bloomix.admin.domain.batch.job.eventsync.dto.EventVenueSyncData;
import com.bloomix.admin.domain.batch.job.kopis.entity.KopisPerformance;
import com.bloomix.admin.domain.batch.job.kopis.repository.KopisFacilityRepository;
import com.bloomix.admin.domain.batch.job.kopis.repository.KopisPerformanceRepository;
import com.bloomix.admin.domain.event.entity.Event;
import com.bloomix.admin.domain.event.entity.EventSourceType;
import com.bloomix.admin.domain.event.entity.EventVenue;
import com.bloomix.admin.domain.event.repository.EventRepository;
import com.bloomix.admin.domain.event.repository.EventVenueRepository;
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

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 행사 반영 배치
 * Step 1 KOPIS 장소 → Step 2 문화정보원 장소 → Step 3 KOPIS 행사 → Step 4 문화정보원 행사
 */
@Configuration
public class EventSyncJobConfig {

  public static final String JOB_NAME = "EVENT_SYNC";

  // 커밋 단위
  private static final int CHUNK_SIZE = 100;

  @Bean
  public Job eventSyncJob(
      JobRepository jobRepository,
      Step kopisVenueSyncStep,
      Step cultureVenueSyncStep,
      Step kopisEventSyncStep,
      Step cultureEventSyncStep
  ) {
    return new JobBuilder(JOB_NAME, jobRepository)
        .start(kopisVenueSyncStep)
        .next(cultureVenueSyncStep)
        .next(kopisEventSyncStep)
        .next(cultureEventSyncStep)
        .build();
  }

  /** [Step 1] KOPIS 장소: 공연시설 → 행사 장소 */
  @Bean
  public Step kopisVenueSyncStep(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      ListItemReader<String> kopisVenueSyncReader,
      ItemWriter<String> kopisVenueSyncWriter
  ) {
    return new StepBuilder("kopisVenueSyncStep", jobRepository)
        .<String, String>chunk(CHUNK_SIZE)
        .transactionManager(transactionManager)
        .reader(kopisVenueSyncReader)
        .writer(kopisVenueSyncWriter)
        .build();
  }

  /** [Step 2] 문화정보원 장소: 문화예술공간 → 행사 장소 */
  @Bean
  public Step cultureVenueSyncStep(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      ListItemReader<String> cultureVenueSyncReader,
      ItemWriter<String> cultureVenueSyncWriter
  ) {
    return new StepBuilder("cultureVenueSyncStep", jobRepository)
        .<String, String>chunk(CHUNK_SIZE)
        .transactionManager(transactionManager)
        .reader(cultureVenueSyncReader)
        .writer(cultureVenueSyncWriter)
        .build();
  }

  /** [Step 3] KOPIS 행사: 공연 → 행사 */
  @Bean
  public Step kopisEventSyncStep(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      ListItemReader<String> kopisEventSyncReader,
      ItemWriter<String> kopisEventSyncWriter
  ) {
    return new StepBuilder("kopisEventSyncStep", jobRepository)
        .<String, String>chunk(CHUNK_SIZE)
        .transactionManager(transactionManager)
        .reader(kopisEventSyncReader)
        .writer(kopisEventSyncWriter)
        .build();
  }

  /** [Step 4] 문화정보원 행사: 문화정보 → 행사 */
  @Bean
  public Step cultureEventSyncStep(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      ListItemReader<String> cultureEventSyncReader,
      ItemWriter<String> cultureEventSyncWriter
  ) {
    return new StepBuilder("cultureEventSyncStep", jobRepository)
        .<String, String>chunk(CHUNK_SIZE)
        .transactionManager(transactionManager)
        .reader(cultureEventSyncReader)
        .writer(cultureEventSyncWriter)
        .build();
  }

  /** KOPIS 장소 Reader */
  @Bean
  @StepScope
  public ListItemReader<String> kopisVenueSyncReader(EventVenueRepository eventVenueRepository) {
    return new ListItemReader<>(eventVenueRepository.findKopisFacilityIdsToSync());
  }

  /** 문화정보원 장소 Reader */
  @Bean
  @StepScope
  public ListItemReader<String> cultureVenueSyncReader(EventVenueRepository eventVenueRepository) {
    return new ListItemReader<>(eventVenueRepository.findCulturePlaceSeqsToSync());
  }

  /** KOPIS 행사 Reader */
  @Bean
  @StepScope
  public ListItemReader<String> kopisEventSyncReader(EventRepository eventRepository) {
    return new ListItemReader<>(eventRepository.findKopisPerformanceIdsToSync());
  }

  /** 문화정보원 행사 Reader */
  @Bean
  @StepScope
  public ListItemReader<String> cultureEventSyncReader(EventRepository eventRepository) {
    return new ListItemReader<>(eventRepository.findCultureInfoSeqsToSync());
  }

  /** KOPIS 장소 Writer */
  @Bean
  public ItemWriter<String> kopisVenueSyncWriter(
      KopisFacilityRepository kopisFacilityRepository,
      EventVenueRepository eventVenueRepository
  ) {
    return chunk -> {
      List<String> ids = Collections.unmodifiableList(chunk.getItems());
      List<EventVenueSyncData> data = kopisFacilityRepository.findAllById(ids).stream()
          .map(EventVenueSyncData::from)
          .toList();

      saveVenues(eventVenueRepository, EventSourceType.KOPIS, data);
    };
  }

  /** 문화정보원 장소 Writer */
  @Bean
  public ItemWriter<String> cultureVenueSyncWriter(
      CulturePlaceRepository culturePlaceRepository,
      EventVenueRepository eventVenueRepository
  ) {
    return chunk -> {
      List<String> ids = Collections.unmodifiableList(chunk.getItems());
      List<EventVenueSyncData> data = culturePlaceRepository.findAllById(ids).stream()
          .map(EventVenueSyncData::from)
          .toList();

      saveVenues(eventVenueRepository, EventSourceType.CULTURE_INFO, data);
    };
  }

  /** KOPIS 행사 Writer */
  @Bean
  public ItemWriter<String> kopisEventSyncWriter(
      KopisPerformanceRepository kopisPerformanceRepository,
      EventVenueRepository eventVenueRepository,
      EventRepository eventRepository
  ) {
    return chunk -> {
      List<String> ids = Collections.unmodifiableList(chunk.getItems());
      List<KopisPerformance> performances = kopisPerformanceRepository.findAllById(ids);

      Map<String, Long> venueIds = findVenueIds(eventVenueRepository, EventSourceType.KOPIS,
          performances.stream().map(KopisPerformance::getFacilityId).toList());

      List<EventSyncData> data = performances.stream()
          .map(performance -> EventSyncData.of(performance, venueIds.get(performance.getFacilityId())))
          .toList();

      saveEvents(eventRepository, EventSourceType.KOPIS, data);
    };
  }

  /** 문화정보원 행사 Writer */
  @Bean
  public ItemWriter<String> cultureEventSyncWriter(
      CultureInfoRepository cultureInfoRepository,
      EventVenueRepository eventVenueRepository,
      EventRepository eventRepository
  ) {
    return chunk -> {
      List<String> ids = Collections.unmodifiableList(chunk.getItems());
      List<CultureInfo> infos = cultureInfoRepository.findAllById(ids);

      Map<String, Long> venueIds = findVenueIds(eventVenueRepository, EventSourceType.CULTURE_INFO,
          infos.stream().map(CultureInfo::getPlaceSeq).toList());

      List<EventSyncData> data = infos.stream()
          .map(info -> EventSyncData.of(info, venueIds.get(info.getPlaceSeq())))
          .toList();

      saveEvents(eventRepository, EventSourceType.CULTURE_INFO, data);
    };
  }

  /** 행사 장소 저장 및 수정 */
  private static void saveVenues(EventVenueRepository eventVenueRepository, EventSourceType sourceType, List<EventVenueSyncData> data) {

    Map<String, EventVenue> existing = eventVenueRepository
        .findAllBySourceTypeAndSourceIdIn(sourceType, data.stream().map(EventVenueSyncData::getSourceId).toList()).stream()
        .collect(Collectors.toMap(EventVenue::getSourceId, Function.identity()));

    List<EventVenue> created = new ArrayList<>();
    for (EventVenueSyncData item : data) {
      EventVenue venue = existing.get(item.getSourceId());
      if (venue != null) venue.update(item);
      else created.add(EventVenue.create(item));
    }

    eventVenueRepository.saveAll(created);
  }

  /** 행사 저장 및 수정 */
  private static void saveEvents(EventRepository eventRepository, EventSourceType sourceType, List<EventSyncData> data) {

    Map<String, Event> existing = eventRepository
        .findAllBySourceTypeAndSourceIdIn(sourceType, data.stream().map(EventSyncData::getSourceId).toList()).stream()
        .collect(Collectors.toMap(Event::getSourceId, Function.identity()));

    List<Event> created = new ArrayList<>();
    for (EventSyncData item : data) {
      Event event = existing.get(item.getSourceId());
      if (event != null) event.update(item);
      else created.add(Event.create(item));
    }

    eventRepository.saveAll(created);
  }

  /** 출처 장소ID에 매핑되는 행사 장소ID 조회 */
  private static Map<String, Long> findVenueIds(EventVenueRepository eventVenueRepository, EventSourceType sourceType, Collection<String> sourceIds) {

    List<String> ids = sourceIds.stream().filter(Objects::nonNull).distinct().toList();
    if (ids.isEmpty()) return Map.of();

    return eventVenueRepository.findAllBySourceTypeAndSourceIdIn(sourceType, ids).stream()
        .collect(Collectors.toMap(EventVenue::getSourceId, EventVenue::getVenueId));
  }
}
