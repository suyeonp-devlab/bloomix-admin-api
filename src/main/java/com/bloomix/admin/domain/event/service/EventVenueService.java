package com.bloomix.admin.domain.event.service;

import com.bloomix.admin.domain.code.service.CommonCodeService;
import com.bloomix.admin.domain.event.dto.EventVenueHistoryResponse;
import com.bloomix.admin.domain.event.dto.EventVenueListRequest;
import com.bloomix.admin.domain.event.dto.EventVenueListResponse;
import com.bloomix.admin.domain.event.dto.EventVenueRequest;
import com.bloomix.admin.domain.event.dto.EventVenueResponse;
import com.bloomix.admin.domain.event.entity.EventVenue;
import com.bloomix.admin.domain.event.entity.EventVenueHistory;
import com.bloomix.admin.domain.event.repository.EventVenueHistoryRepository;
import com.bloomix.admin.domain.event.repository.EventVenueRepository;
import com.bloomix.admin.exception.BizException;
import com.bloomix.admin.exception.ErrorCode;
import com.bloomix.admin.response.PageResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventVenueService {

  private static final String SOURCE_TYPE_GROUP_CODE = "EVENT_SOURCE";
  private static final String SIDO_GROUP_CODE = "SIDO";

  private final EventVenueRepository eventVenueRepository;
  private final EventVenueHistoryRepository eventVenueHistoryRepository;
  private final CommonCodeService commonCodeService;

  /** 장소 목록 조회 */
  public PageResponse<EventVenueListResponse> searchVenues(EventVenueListRequest condition, Pageable pageable) {

    Map<String, String> sourceTypeNames = commonCodeService.getCodeNameMap(SOURCE_TYPE_GROUP_CODE);
    Map<String, String> sidoNames = commonCodeService.getCodeNameMap(SIDO_GROUP_CODE);

    return PageResponse.from(eventVenueRepository.searchVenues(condition, pageable)
        .map(venue -> EventVenueListResponse.of(venue, sourceTypeNames, sidoNames)));
  }

  /** 장소 상세 조회 */
  public EventVenueResponse getVenue(Long venueId) {
    return EventVenueResponse.of(
        findVenue(venueId),
        commonCodeService.getCodeNameMap(SOURCE_TYPE_GROUP_CODE),
        commonCodeService.getCodeNameMap(SIDO_GROUP_CODE));
  }

  /** 장소 수정 */
  @Transactional
  public void updateVenue(Long venueId, EventVenueRequest request) {

    validateVenue(request);

    EventVenue venue = findVenue(venueId);
    venue.update(request);

    eventVenueHistoryRepository.save(EventVenueHistory.create(venue));
  }

  /** 장소 변경 이력 조회 */
  public PageResponse<EventVenueHistoryResponse> searchHistories(Long venueId, Pageable pageable) {

    if (!eventVenueRepository.existsById(venueId)) {
      throw new BizException(ErrorCode.EVENT_VENUE_NOT_FOUND);
    }

    Map<String, String> sidoNames = commonCodeService.getCodeNameMap(SIDO_GROUP_CODE);

    return PageResponse.from(eventVenueHistoryRepository.findByVenueIdOrderByHistoryIdDesc(venueId, pageable)
        .map(history -> EventVenueHistoryResponse.of(history, sidoNames)));
  }

  /** 장소 조회 */
  private EventVenue findVenue(Long venueId) {
    return eventVenueRepository.findById(venueId)
        .orElseThrow(() -> new BizException(ErrorCode.EVENT_VENUE_NOT_FOUND));
  }

  /** 장소 수정 검사 */
  private void validateVenue(EventVenueRequest request) {

    // 시도 코드
    if (request.sidoCode() != null && !commonCodeService.getCodeNameMap(SIDO_GROUP_CODE).containsKey(request.sidoCode())) {
      throw new BizException(ErrorCode.INVALID_INPUT, "시도가 올바르지 않습니다.");
    }
  }
}
