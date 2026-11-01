package com.bloomix.admin.domain.batch.job.eventsync;

import com.bloomix.admin.domain.batch.job.cultureinfo.entity.CultureInfo;
import com.bloomix.admin.domain.batch.job.cultureinfo.repository.CultureInfoRepository;
import com.bloomix.admin.domain.batch.job.cultureinfo.repository.CulturePlaceRepository;
import com.bloomix.admin.domain.batch.job.eventsync.dto.EventSyncData;
import com.bloomix.admin.domain.batch.job.eventsync.dto.EventVenueSyncData;
import com.bloomix.admin.domain.batch.job.kopis.entity.KopisFacility;
import com.bloomix.admin.domain.batch.job.kopis.entity.KopisPerformance;
import com.bloomix.admin.domain.batch.job.kopis.repository.KopisFacilityRepository;
import com.bloomix.admin.domain.batch.job.kopis.repository.KopisPerformanceRepository;
import com.bloomix.admin.domain.event.entity.*;
import com.bloomix.admin.domain.event.repository.*;
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
import org.springframework.util.StringUtils;

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
      EventVenueRepository eventVenueRepository,
      EventVenueHistoryRepository eventVenueHistoryRepository
  ) {
    return chunk -> {
      List<String> ids = Collections.unmodifiableList(chunk.getItems());
      List<EventVenueSyncData> data = kopisFacilityRepository.findAllById(ids).stream()
          .map(EventVenueSyncData::from)
          .toList();

      saveVenues(eventVenueRepository, eventVenueHistoryRepository, EventSourceType.KOPIS, data);
    };
  }

  /** 문화정보원 장소 Writer */
  @Bean
  public ItemWriter<String> cultureVenueSyncWriter(
      CulturePlaceRepository culturePlaceRepository,
      EventVenueRepository eventVenueRepository,
      EventVenueHistoryRepository eventVenueHistoryRepository
  ) {
    return chunk -> {
      List<String> ids = Collections.unmodifiableList(chunk.getItems());
      List<EventVenueSyncData> data = culturePlaceRepository.findAllById(ids).stream()
          .map(EventVenueSyncData::from)
          .toList();

      saveVenues(eventVenueRepository, eventVenueHistoryRepository, EventSourceType.CULTURE_INFO, data);
    };
  }

  /** KOPIS 행사 Writer */
  @Bean
  public ItemWriter<String> kopisEventSyncWriter(
      KopisPerformanceRepository kopisPerformanceRepository,
      KopisFacilityRepository kopisFacilityRepository,
      EventVenueRepository eventVenueRepository,
      EventRepository eventRepository,
      EventHistoryRepository eventHistoryRepository,
      EventSourceChangeRepository eventSourceChangeRepository
  ) {
    return chunk -> {
      List<String> ids = Collections.unmodifiableList(chunk.getItems());
      List<KopisPerformance> performances = kopisPerformanceRepository.findAllById(ids);

      List<String> facilityIds = performances.stream()
          .map(KopisPerformance::getFacilityId)
          .filter(Objects::nonNull)
          .distinct()
          .toList();

      // 공연시설 조회
      Map<String, KopisFacility> facilities = kopisFacilityRepository.findAllById(facilityIds).stream()
          .collect(Collectors.toMap(KopisFacility::getFacilityId, Function.identity()));

      // 행사 장소ID 조회
      Map<String, Long> venueIds = findVenueIds(eventVenueRepository, EventSourceType.KOPIS, facilityIds);

      List<EventSyncData> data = performances.stream()
          .map(performance -> EventSyncData.of(
              performance,
              facilities.get(performance.getFacilityId()),
              venueIds.get(performance.getFacilityId())))
          .toList();

      saveEvents(eventRepository, eventHistoryRepository, eventSourceChangeRepository, EventSourceType.KOPIS, data);
    };
  }

  /** 문화정보원 행사 Writer */
  @Bean
  public ItemWriter<String> cultureEventSyncWriter(
      CultureInfoRepository cultureInfoRepository,
      EventVenueRepository eventVenueRepository,
      EventVenueHistoryRepository eventVenueHistoryRepository,
      EventRepository eventRepository,
      EventHistoryRepository eventHistoryRepository,
      EventSourceChangeRepository eventSourceChangeRepository
  ) {
    return chunk -> {
      List<String> ids = Collections.unmodifiableList(chunk.getItems());
      List<CultureInfo> infos = cultureInfoRepository.findAllById(ids);

      // 문화예술공간이 없는 문화정보 > 문화정보의 장소명 & 지역으로 장소 생성
      List<EventVenueSyncData> infoVenues = infos.stream()
          .filter(info -> !hasCulturePlace(info) && (StringUtils.hasText(info.getPlace()) || StringUtils.hasText(info.getArea())))
          .map(EventVenueSyncData::from)
          .toList();

      saveVenues(eventVenueRepository, eventVenueHistoryRepository, EventSourceType.CULTURE_INFO_UNREGISTERED, infoVenues);

      // 행사 장소ID 조회 (CULTURE_INFO, CULTURE_INFO_UNREGISTERED)
      Map<String, Long> placeVenueIds = findVenueIds(eventVenueRepository, EventSourceType.CULTURE_INFO,
          infos.stream().map(CultureInfo::getPlaceSeq).toList());

      Map<String, Long> infoVenueIds = findVenueIds(eventVenueRepository, EventSourceType.CULTURE_INFO_UNREGISTERED,
          infoVenues.stream().map(EventVenueSyncData::getSourceId).toList());

      // 문화정보와 행사 장소ID 연결
      List<EventSyncData> data = infos.stream()
          .map(info -> EventSyncData.of(info, hasCulturePlace(info)
              ? placeVenueIds.get(info.getPlaceSeq())
              : infoVenueIds.get(info.getSeq())))
          .toList();

      saveEvents(eventRepository, eventHistoryRepository, eventSourceChangeRepository, EventSourceType.CULTURE_INFO, data);
    };
  }

  /** 행사 장소 저장 */
  private static void saveVenues(
      EventVenueRepository eventVenueRepository,
      EventVenueHistoryRepository eventVenueHistoryRepository,
      EventSourceType sourceType,
      List<EventVenueSyncData> data
  ) {

    if (data.isEmpty()) return;

    Set<String> existing = eventVenueRepository
        .findAllBySourceTypeAndSourceIdIn(sourceType, data.stream().map(EventVenueSyncData::getSourceId).toList()).stream()
        .map(EventVenue::getSourceId)
        .collect(Collectors.toSet());

    // 신규 장소 추출
    List<EventVenue> created = data.stream()
        .filter(item -> !existing.contains(item.getSourceId()))
        .map(EventVenue::create)
        .toList();

    // 신규 장소 저장 및 이력 생성
    List<EventVenueHistory> histories = eventVenueRepository.saveAll(created).stream()
        .map(EventVenueHistory::create)
        .toList();

    eventVenueHistoryRepository.saveAll(histories);
  }

  /** 행사 저장 */
  private static void saveEvents(
      EventRepository eventRepository,
      EventHistoryRepository eventHistoryRepository,
      EventSourceChangeRepository eventSourceChangeRepository,
      EventSourceType sourceType,
      List<EventSyncData> data
  ) {

    if (data.isEmpty()) return;

    Map<String, Event> existing = eventRepository
        .findAllBySourceTypeAndSourceIdIn(sourceType, data.stream().map(EventSyncData::getSourceId).toList()).stream()
        .collect(Collectors.toMap(Event::getSourceId, Function.identity()));
    
    Map<Long, EventSourceChange> pending = findPendingChanges(eventSourceChangeRepository, existing.values());

    // 신규 행사 추출 + 기존 행사 변경 감지
    List<Event> created = new ArrayList<>();
    List<EventSourceChange> changes = new ArrayList<>();
    for (EventSyncData item : data) {
      Event event = existing.get(item.getSourceId());
      if (event == null) {
        created.add(Event.create(item));
        continue;
      }

      event.markChecked();

      // 기존 행사 중 원본 기간·장소가 다르면 변경 후보 기록
      EventSourceChange change = pending.get(event.getEventId());
      if (event.isChangedBy(item)) {
        if (change != null) change.update(item);
        else changes.add(EventSourceChange.create(event.getEventId(), item));
      } else if (change != null) {
        // 원본이 현재 값으로 돌아오면 대기 중인 후보 정리
        change.ignore();
      }
    }

    // 신규 행사 저장 및 이력 저장
    List<EventHistory> histories = eventRepository.saveAll(created).stream()
        .map(EventHistory::create)
        .toList();

    eventHistoryRepository.saveAll(histories);
    eventSourceChangeRepository.saveAll(changes);
  }

  /** 행사ID별 대기 중인 변경 후보 조회 */
  private static Map<Long, EventSourceChange> findPendingChanges(
      EventSourceChangeRepository eventSourceChangeRepository,
      Collection<Event> events
  ) {

    if (events.isEmpty()) return Map.of();

    List<Long> eventIds = events.stream().map(Event::getEventId).toList();

    return eventSourceChangeRepository.findAllByEventIdInAndStatus(eventIds, EventSourceChangeStatus.PENDING).stream()
        .collect(Collectors.toMap(EventSourceChange::getEventId, Function.identity()));
  }

  /** 출처 장소ID에 매핑되는 행사 장소ID 조회 */
  private static Map<String, Long> findVenueIds(EventVenueRepository eventVenueRepository, EventSourceType sourceType, Collection<String> sourceIds) {

    List<String> ids = sourceIds.stream().filter(Objects::nonNull).distinct().toList();
    if (ids.isEmpty()) return Map.of();

    return eventVenueRepository.findAllBySourceTypeAndSourceIdIn(sourceType, ids).stream()
        .collect(Collectors.toMap(EventVenue::getSourceId, EventVenue::getVenueId));
  }

  /** 문화예술공간 연결 여부 (문화정보원은 공간이 없으면 "0") */
  private static boolean hasCulturePlace(CultureInfo info) {
    return StringUtils.hasText(info.getPlaceSeq()) && !"0".equals(info.getPlaceSeq());
  }
}
