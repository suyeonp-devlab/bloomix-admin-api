package com.bloomix.admin.domain.event.dto;

import com.bloomix.admin.domain.event.entity.Event;
import com.bloomix.admin.domain.event.entity.EventSourceChange;
import com.bloomix.admin.domain.event.entity.EventVenue;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record EventSourceChangeResponse(
  Long changeId,
  Long eventId,
  LocalDate currentStartDate,
  LocalDate currentEndDate,
  Long currentVenueId,
  String currentVenueName,
  LocalDate sourceStartDate,
  LocalDate sourceEndDate,
  Long sourceVenueId,
  String sourceVenueName,
  LocalDateTime detectedAt
) {

  public static EventSourceChangeResponse of(
      EventSourceChange change,
      Event event,
      EventVenue currentVenue,
      EventVenue sourceVenue
  ) {
    return new EventSourceChangeResponse(
        change.getChangeId(),
        event.getEventId(),
        event.getStartDate(),
        event.getEndDate(),
        event.getVenueId(),
        currentVenue == null ? null : currentVenue.getName(),
        change.getStartDate(),
        change.getEndDate(),
        change.getVenueId(),
        sourceVenue == null ? null : sourceVenue.getName(),
        change.getUpdatedAt()
    );
  }
}
