package com.bloomix.admin.domain.event.dto;

import com.bloomix.admin.domain.event.entity.EventSourceType;
import com.bloomix.admin.domain.event.entity.EventVenue;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public record EventVenueResponse(
  Long venueId,
  EventSourceType sourceType,
  String sourceTypeName,
  String sourceId,
  String name,
  String address,
  String sidoCode,
  String sidoName,
  String sigungu,
  BigDecimal latitude,
  BigDecimal longitude,
  String phone,
  String homepageUrl,
  Integer seatScale,
  boolean verified,
  String updatedBy,
  LocalDateTime updatedAt
) {

  public static EventVenueResponse of(
      EventVenue venue,
      Map<String, String> sourceTypeNames,
      Map<String, String> sidoNames
  ) {
    return new EventVenueResponse(
        venue.getVenueId(),
        venue.getSourceType(),
        sourceTypeNames.get(venue.getSourceType().name()),
        venue.getSourceId(),
        venue.getName(),
        venue.getAddress(),
        venue.getSidoCode(),
        sidoNames.get(venue.getSidoCode()),
        venue.getSigungu(),
        venue.getLatitude(),
        venue.getLongitude(),
        venue.getPhone(),
        venue.getHomepageUrl(),
        venue.getSeatScale(),
        venue.isVerified(),
        venue.getUpdatedBy(),
        venue.getUpdatedAt()
    );
  }
}
