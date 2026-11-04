package com.bloomix.admin.domain.event.dto;

import com.bloomix.admin.domain.event.entity.EventHistory;
import com.bloomix.admin.domain.event.entity.EventVenue;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record EventHistoryResponse(
  Long historyId,
  String title,
  LocalDate startDate,
  LocalDate endDate,
  Long venueId,
  String venueName,
  int capacity,
  boolean onSale,
  String saleStopReason,
  boolean enabled,
  boolean verified,
  String changedBy,
  LocalDateTime changedAt
) {

  public static EventHistoryResponse of(EventHistory history, EventVenue venue) {
    return new EventHistoryResponse(
        history.getHistoryId(),
        history.getTitle(),
        history.getStartDate(),
        history.getEndDate(),
        history.getVenueId(),
        venue == null ? null : venue.getName(),
        history.getCapacity(),
        history.isOnSale(),
        history.getSaleStopReason(),
        history.isEnabled(),
        history.isVerified(),
        history.getCreatedBy(),
        history.getCreatedAt()
    );
  }
}
