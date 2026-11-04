package com.bloomix.admin.domain.event.service;

import com.bloomix.admin.domain.code.service.CommonCodeService;
import com.bloomix.admin.domain.event.dto.EventHistoryResponse;
import com.bloomix.admin.domain.event.dto.EventListRequest;
import com.bloomix.admin.domain.event.dto.EventListResponse;
import com.bloomix.admin.domain.event.dto.EventRequest;
import com.bloomix.admin.domain.event.dto.EventResponse;
import com.bloomix.admin.domain.event.dto.EventSaleRequest;
import com.bloomix.admin.domain.event.dto.EventSourceChangeResponse;
import com.bloomix.admin.domain.event.dto.EventSourceChangeStatusRequest;
import com.bloomix.admin.domain.event.entity.Event;
import com.bloomix.admin.domain.event.entity.EventHistory;
import com.bloomix.admin.domain.event.entity.EventSchedule;
import com.bloomix.admin.domain.event.entity.EventSourceChange;
import com.bloomix.admin.domain.event.entity.EventSourceChangeStatus;
import com.bloomix.admin.domain.event.entity.EventVenue;
import com.bloomix.admin.domain.event.repository.EventHistoryRepository;
import com.bloomix.admin.domain.event.repository.EventRepository;
import com.bloomix.admin.domain.event.repository.EventScheduleRepository;
import com.bloomix.admin.domain.event.repository.EventSourceChangeRepository;
import com.bloomix.admin.domain.event.repository.EventVenueRepository;
import com.bloomix.admin.exception.BizException;
import com.bloomix.admin.exception.ErrorCode;
import com.bloomix.admin.response.PageResponse;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

  private static final String SOURCE_TYPE_GROUP_CODE = "EVENT_SOURCE";
  private static final String CATEGORY_GROUP_CODE = "EVENT_CATEGORY";
  private static final String SIDO_GROUP_CODE = "SIDO";

  private final EventRepository eventRepository;
  private final EventVenueRepository eventVenueRepository;
  private final EventHistoryRepository eventHistoryRepository;
  private final EventSourceChangeRepository eventSourceChangeRepository;
  private final EventScheduleRepository eventScheduleRepository;
  private final CommonCodeService commonCodeService;

  /** 행사 목록 조회 */
  public PageResponse<EventListResponse> searchEvents(EventListRequest condition, Pageable pageable) {

    Map<String, String> sourceTypeNames = commonCodeService.getCodeNameMap(SOURCE_TYPE_GROUP_CODE);
    Map<String, String> categoryNames = commonCodeService.getCodeNameMap(CATEGORY_GROUP_CODE);
    Map<String, String> sidoNames = commonCodeService.getCodeNameMap(SIDO_GROUP_CODE);

    Page<Event> events = eventRepository.searchEvents(condition, pageable);

    // 장소
    Map<Long, EventVenue> venues = findVenues(events.getContent().stream().map(Event::getVenueId).toList());

    // 원본 변경 후보가 있는 행사
    Set<Long> changedEventIds = findChangedEventIds(events.getContent().stream().map(Event::getEventId).toList());

    return PageResponse.from(events.map(event -> EventListResponse.of(
        event,
        venues.get(event.getVenueId()),
        changedEventIds.contains(event.getEventId()),
        sourceTypeNames,
        categoryNames,
        sidoNames)));
  }

  /** 행사 상세 조회 */
  public EventResponse getEvent(Long eventId) {

    Event event = findEvent(eventId);
    EventVenue venue = Optional.ofNullable(event.getVenueId()).flatMap(eventVenueRepository::findById).orElse(null);

    return EventResponse.of(
        event,
        venue,
        eventSourceChangeRepository.existsByEventIdAndStatus(eventId, EventSourceChangeStatus.PENDING),
        commonCodeService.getCodeNameMap(SOURCE_TYPE_GROUP_CODE),
        commonCodeService.getCodeNameMap(CATEGORY_GROUP_CODE),
        commonCodeService.getCodeNameMap(SIDO_GROUP_CODE));
  }

  /** 행사 수정 */
  @Transactional
  public void updateEvent(Long eventId, EventRequest request) {

    validateEvent(request);

    Event event = findEvent(eventId);
    event.update(request);

    disableSchedulesOutOfPeriod(event);
    eventHistoryRepository.save(EventHistory.create(event));
  }

  /** 행사 판매 중단, 판매 재개 */
  @Transactional
  public void updateSale(Long eventId, EventSaleRequest request) {

    Event event = findEvent(eventId);
    validateSale(event, request);

    if (request.onSale()) {
      event.resumeSale();
    } else {
      event.stopSale(request.saleStopReason());
    }

    eventHistoryRepository.save(EventHistory.create(event));
  }

  /** 행사 변경 이력 조회 */
  public PageResponse<EventHistoryResponse> searchHistories(Long eventId, Pageable pageable) {

    if (!eventRepository.existsById(eventId)) {
      throw new BizException(ErrorCode.EVENT_NOT_FOUND);
    }

    Page<EventHistory> histories = eventHistoryRepository.findByEventIdOrderByHistoryIdDesc(eventId, pageable);

    // 장소
    Map<Long, EventVenue> venues = findVenues(histories.getContent().stream().map(EventHistory::getVenueId).toList());

    return PageResponse.from(histories.map(history -> EventHistoryResponse.of(history, venues.get(history.getVenueId()))));
  }

  /** 대기 중인 원본 변경 후보 조회 (없으면 null) */
  public EventSourceChangeResponse getSourceChange(Long eventId) {

    Event event = findEvent(eventId);

    return eventSourceChangeRepository.findByEventIdAndStatus(eventId, EventSourceChangeStatus.PENDING)
        .map(change -> {
          Map<Long, EventVenue> venues = findVenues(Stream.of(event.getVenueId(), change.getVenueId()).toList());
          return EventSourceChangeResponse.of(change, event, venues.get(event.getVenueId()), venues.get(change.getVenueId()));
        })
        .orElse(null);
  }

  /** 원본 변경 후보 반영, 무시 */
  @Transactional
  public void updateSourceChangeStatus(Long changeId, EventSourceChangeStatusRequest request) {

    EventSourceChange change = eventSourceChangeRepository.findById(changeId)
        .orElseThrow(() -> new BizException(ErrorCode.EVENT_SOURCE_CHANGE_NOT_FOUND));

    validateSourceChangeStatus(change, request.status());

    if (request.status() == EventSourceChangeStatus.IGNORED) {
      change.ignore();
      return;
    }

    // 원본 기간·장소 반영
    Event event = findEvent(change.getEventId());
    event.applySourceChange(change);
    change.apply();

    disableSchedulesOutOfPeriod(event);
    eventHistoryRepository.save(EventHistory.create(event));
  }

  /** 행사 조회 */
  private Event findEvent(Long eventId) {
    return eventRepository.findById(eventId)
        .orElseThrow(() -> new BizException(ErrorCode.EVENT_NOT_FOUND));
  }

  /** 장소ID별 장소 조회 */
  private Map<Long, EventVenue> findVenues(Collection<Long> venueIds) {

    List<Long> ids = venueIds.stream().filter(Objects::nonNull).distinct().toList();

    return eventVenueRepository.findAllById(ids).stream()
        .collect(Collectors.toMap(EventVenue::getVenueId, Function.identity()));
  }

  /** 원본 변경 후보가 있는 행사ID 조회 */
  private Set<Long> findChangedEventIds(List<Long> eventIds) {

    if (eventIds.isEmpty()) return Set.of();

    return eventSourceChangeRepository.findAllByEventIdInAndStatus(eventIds, EventSourceChangeStatus.PENDING).stream()
        .map(EventSourceChange::getEventId)
        .collect(Collectors.toSet());
  }

  /** 행사 기간 밖 회차 사용 중지 */
  private void disableSchedulesOutOfPeriod(Event event) {
    eventScheduleRepository.findAllByEventIdAndEnabledTrue(event.getEventId()).stream()
        .filter(schedule -> schedule.getScheduleDate().isBefore(event.getStartDate())
            || schedule.getScheduleDate().isAfter(event.getEndDate()))
        .forEach(EventSchedule::disable);
  }

  /** 행사 수정 검사 */
  private void validateEvent(EventRequest request) {

    if (request.endDate().isBefore(request.startDate())) {
      throw new BizException(ErrorCode.INVALID_INPUT, "종료일은 시작일보다 빠를 수 없습니다.");
    }
  }

  /** 판매 중단, 판매 재개 검사 */
  private void validateSale(Event event, EventSaleRequest request) {

    if (event.isOnSale() == request.onSale()) {
      throw new BizException(ErrorCode.INVALID_EVENT_SALE_CHANGE);
    }

    if (!request.onSale() && request.saleStopReason() == null) {
      throw new BizException(ErrorCode.INVALID_INPUT, "판매 중단 사유를 입력해주세요.");
    }
  }

  /** 원본 변경 후보 처리 검사 */
  private void validateSourceChangeStatus(EventSourceChange change, EventSourceChangeStatus status) {

    if (change.getStatus() != EventSourceChangeStatus.PENDING) {
      throw new BizException(ErrorCode.EVENT_SOURCE_CHANGE_ALREADY_PROCESSED);
    }

    if (status == EventSourceChangeStatus.PENDING) {
      throw new BizException(ErrorCode.INVALID_INPUT, "반영 또는 무시만 선택할 수 있습니다.");
    }
  }
}
