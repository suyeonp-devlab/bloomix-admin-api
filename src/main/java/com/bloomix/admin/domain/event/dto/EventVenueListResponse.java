package com.bloomix.admin.domain.event.dto;

import com.bloomix.admin.domain.event.entity.EventSourceType;
import com.bloomix.admin.domain.event.entity.EventVenue;
import java.time.LocalDateTime;
import java.util.Map;

public record EventVenueListResponse(
  Long venueId,
  EventSourceType sourceType,
  String sourceTypeName,
  String sourceId,
  String name,
  String address,
  String sidoCode,
  String sidoName,
  String sigungu,
  boolean verified,
  String updatedBy,
  LocalDateTime updatedAt
) {

  public static EventVenueListResponse of(
      EventVenue venue,
      Map<String, String> sourceTypeNames,
      Map<String, String> sidoNames
  ) {
    return new EventVenueListResponse(
        venue.getVenueId(),
        venue.getSourceType(),
        sourceTypeNames.get(venue.getSourceType().name()),
        venue.getSourceId(),
        venue.getName(),
        venue.getAddress(),
        venue.getSidoCode(),
        sidoNames.get(venue.getSidoCode()),
        venue.getSigungu(),
        venue.isVerified(),
        venue.getUpdatedBy(),
        venue.getUpdatedAt()
    );
  }
}
