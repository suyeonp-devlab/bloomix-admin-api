package com.bloomix.admin.domain.event.service;

import com.bloomix.admin.domain.event.dto.EventScheduleListRequest;
import com.bloomix.admin.domain.event.dto.EventScheduleRequest;
import com.bloomix.admin.domain.event.dto.EventScheduleResponse;
import com.bloomix.admin.domain.event.entity.Event;
import com.bloomix.admin.domain.event.entity.EventSchedule;
import com.bloomix.admin.domain.event.repository.EventRepository;
import com.bloomix.admin.domain.event.repository.EventScheduleRepository;
import com.bloomix.admin.exception.BizException;
import com.bloomix.admin.exception.ErrorCode;
import com.bloomix.admin.response.PageResponse;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventScheduleService {

  private final EventRepository eventRepository;
  private final EventScheduleRepository eventScheduleRepository;

  /** 회차 목록 조회 */
  public PageResponse<EventScheduleResponse> searchSchedules(Long eventId, EventScheduleListRequest condition, Pageable pageable) {

    if (!eventRepository.existsById(eventId)) {
      throw new BizException(ErrorCode.EVENT_NOT_FOUND);
    }

    return PageResponse.from(eventScheduleRepository.searchSchedules(eventId, condition, pageable)
        .map(EventScheduleResponse::from));
  }

  /** 회차 등록 */
  @Transactional
  public void createSchedule(Long eventId, EventScheduleRequest request) {

    Event event = findEvent(eventId);
    validateSchedule(event, null, request);

    eventScheduleRepository.save(EventSchedule.create(eventId, request));
  }

  /** 회차 수정 */
  @Transactional
  public void updateSchedule(Long scheduleId, EventScheduleRequest request) {

    EventSchedule schedule = eventScheduleRepository.findById(scheduleId)
        .orElseThrow(() -> new BizException(ErrorCode.EVENT_SCHEDULE_NOT_FOUND));

    Event event = findEvent(schedule.getEventId());
    validateSchedule(event, scheduleId, request);

    schedule.update(request);
  }

  /** 행사 조회 */
  private Event findEvent(Long eventId) {
    return eventRepository.findById(eventId)
        .orElseThrow(() -> new BizException(ErrorCode.EVENT_NOT_FOUND));
  }

  /** 회차 등록 및 수정 검사 (수정이 아니면 scheduleId는 null) */
  private void validateSchedule(Event event, Long scheduleId, EventScheduleRequest request) {

    // 행사 기간
    if (request.scheduleDate().isBefore(event.getStartDate()) || request.scheduleDate().isAfter(event.getEndDate())) {
      throw new BizException(ErrorCode.INVALID_INPUT,
          "회차 날짜는 행사 기간(" + event.getStartDate() + " ~ " + event.getEndDate() + ") 안이어야 합니다.");
    }

    // 같은 날짜·시간 회차 중복
    boolean duplicated = eventScheduleRepository.findAllByEventIdAndScheduleDate(event.getEventId(), request.scheduleDate()).stream()
        .anyMatch(schedule -> Objects.equals(schedule.getStartTime(), request.startTime())
            && !schedule.getScheduleId().equals(scheduleId));

    if (duplicated) {
      throw new BizException(ErrorCode.DUPLICATE_EVENT_SCHEDULE);
    }
  }
}
