package com.bloomix.admin.domain.event.dto;

import com.bloomix.admin.domain.event.entity.Event;
import com.bloomix.admin.domain.event.entity.EventCategory;
import com.bloomix.admin.domain.event.entity.EventSourceType;
import com.bloomix.admin.domain.event.entity.EventVenue;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

public record EventListResponse(
  Long eventId,
  EventSourceType sourceType,
  String sourceTypeName,
  String sourceId,
  EventCategory category,
  String categoryName,
  String title,
  LocalDate startDate,
  LocalDate endDate,
  Long venueId,
  String venueName,
  String sidoCode,
  String sidoName,
  int capacity,
  boolean onSale,
  boolean enabled,
  boolean verified,
  boolean hasSourceChange,
  String updatedBy,
  LocalDateTime updatedAt
) {

  public static EventListResponse of(
      Event event,
      EventVenue venue,
      boolean hasSourceChange,
      Map<String, String> sourceTypeNames,
      Map<String, String> categoryNames,
      Map<String, String> sidoNames
  ) {
    return new EventListResponse(
        event.getEventId(),
        event.getSourceType(),
        sourceTypeNames.get(event.getSourceType().name()),
        event.getSourceId(),
        event.getCategory(),
        categoryNames.get(event.getCategory().name()),
        event.getTitle(),
        event.getStartDate(),
        event.getEndDate(),
        event.getVenueId(),
        venue == null ? null : venue.getName(),
        venue == null ? null : venue.getSidoCode(),
        venue == null ? null : sidoNames.get(venue.getSidoCode()),
        event.getCapacity(),
        event.isOnSale(),
        event.isEnabled(),
        event.isVerified(),
        hasSourceChange,
        event.getUpdatedBy(),
        event.getUpdatedAt()
    );
  }
}
