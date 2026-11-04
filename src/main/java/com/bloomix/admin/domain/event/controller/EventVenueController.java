package com.bloomix.admin.domain.event.controller;

import com.bloomix.admin.domain.event.dto.EventVenueHistoryResponse;
import com.bloomix.admin.domain.event.dto.EventVenueListRequest;
import com.bloomix.admin.domain.event.dto.EventVenueListResponse;
import com.bloomix.admin.domain.event.dto.EventVenueRequest;
import com.bloomix.admin.domain.event.dto.EventVenueResponse;
import com.bloomix.admin.domain.event.service.EventVenueService;
import com.bloomix.admin.response.ApiResponse;
import com.bloomix.admin.response.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/event-venues")
@RequiredArgsConstructor
public class EventVenueController {

  private final EventVenueService eventVenueService;

  /** 장소 목록 조회 */
  @GetMapping
  public ApiResponse<PageResponse<EventVenueListResponse>> searchVenues(
      @ParameterObject EventVenueListRequest condition,
      @ParameterObject @PageableDefault(size = 20) Pageable pageable
  ) {
    return ApiResponse.success(eventVenueService.searchVenues(condition, pageable));
  }

  /** 장소 상세 조회 */
  @GetMapping("/{venueId}")
  public ApiResponse<EventVenueResponse> getVenue(@PathVariable Long venueId) {
    return ApiResponse.success(eventVenueService.getVenue(venueId));
  }

  /** 장소 수정 */
  @PutMapping("/{venueId}")
  public ApiResponse<Void> updateVenue(
      @PathVariable Long venueId,
      @Valid @RequestBody EventVenueRequest request
  ) {
    eventVenueService.updateVenue(venueId, request);
    return ApiResponse.success(null);
  }

  /** 장소 변경 이력 조회 */
  @GetMapping("/{venueId}/histories")
  public ApiResponse<PageResponse<EventVenueHistoryResponse>> searchHistories(
      @PathVariable Long venueId,
      @ParameterObject @PageableDefault(size = 20) Pageable pageable
  ) {
    return ApiResponse.success(eventVenueService.searchHistories(venueId, pageable));
  }
}
