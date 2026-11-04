package com.bloomix.admin.domain.event.controller;

import com.bloomix.admin.domain.event.dto.EventHistoryResponse;
import com.bloomix.admin.domain.event.dto.EventListRequest;
import com.bloomix.admin.domain.event.dto.EventListResponse;
import com.bloomix.admin.domain.event.dto.EventRequest;
import com.bloomix.admin.domain.event.dto.EventResponse;
import com.bloomix.admin.domain.event.dto.EventSaleRequest;
import com.bloomix.admin.domain.event.dto.EventScheduleListRequest;
import com.bloomix.admin.domain.event.dto.EventScheduleRequest;
import com.bloomix.admin.domain.event.dto.EventScheduleResponse;
import com.bloomix.admin.domain.event.dto.EventSourceChangeResponse;
import com.bloomix.admin.domain.event.service.EventScheduleService;
import com.bloomix.admin.domain.event.service.EventService;
import com.bloomix.admin.response.ApiResponse;
import com.bloomix.admin.response.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

  private final EventService eventService;
  private final EventScheduleService eventScheduleService;

  /** 행사 목록 조회 */
  @GetMapping
  public ApiResponse<PageResponse<EventListResponse>> searchEvents(
      @ParameterObject EventListRequest condition,
      @ParameterObject @PageableDefault(size = 20) Pageable pageable
  ) {
    return ApiResponse.success(eventService.searchEvents(condition, pageable));
  }

  /** 행사 상세 조회 */
  @GetMapping("/{eventId}")
  public ApiResponse<EventResponse> getEvent(@PathVariable Long eventId) {
    return ApiResponse.success(eventService.getEvent(eventId));
  }

  /** 행사 수정 */
  @PutMapping("/{eventId}")
  public ApiResponse<Void> updateEvent(
      @PathVariable Long eventId,
      @Valid @RequestBody EventRequest request
  ) {
    eventService.updateEvent(eventId, request);
    return ApiResponse.success(null);
  }

  /** 행사 판매 중단, 판매 재개 */
  @PutMapping("/{eventId}/sale")
  public ApiResponse<Void> updateSale(
      @PathVariable Long eventId,
      @Valid @RequestBody EventSaleRequest request
  ) {
    eventService.updateSale(eventId, request);
    return ApiResponse.success(null);
  }

  /** 행사 변경 이력 조회 */
  @GetMapping("/{eventId}/histories")
  public ApiResponse<PageResponse<EventHistoryResponse>> searchHistories(
      @PathVariable Long eventId,
      @ParameterObject @PageableDefault(size = 20) Pageable pageable
  ) {
    return ApiResponse.success(eventService.searchHistories(eventId, pageable));
  }

  /** 대기 중인 원본 변경 후보 조회 */
  @GetMapping("/{eventId}/source-change")
  public ApiResponse<EventSourceChangeResponse> getSourceChange(@PathVariable Long eventId) {
    return ApiResponse.success(eventService.getSourceChange(eventId));
  }

  /** 회차 목록 조회 */
  @GetMapping("/{eventId}/schedules")
  public ApiResponse<PageResponse<EventScheduleResponse>> searchSchedules(
      @PathVariable Long eventId,
      @ParameterObject EventScheduleListRequest condition,
      @ParameterObject @PageableDefault(size = 20) Pageable pageable
  ) {
    return ApiResponse.success(eventScheduleService.searchSchedules(eventId, condition, pageable));
  }

  /** 회차 등록 */
  @PostMapping("/{eventId}/schedules")
  public ApiResponse<Void> createSchedule(
      @PathVariable Long eventId,
      @Valid @RequestBody EventScheduleRequest request
  ) {
    eventScheduleService.createSchedule(eventId, request);
    return ApiResponse.success(null);
  }
}
