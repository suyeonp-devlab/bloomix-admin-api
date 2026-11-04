package com.bloomix.admin.domain.event.dto;

import com.bloomix.admin.domain.event.entity.Event;
import com.bloomix.admin.domain.event.entity.EventCategory;
import com.bloomix.admin.domain.event.entity.EventSourceType;
import com.bloomix.admin.domain.event.entity.EventVenue;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record EventResponse(
  Long eventId,
  EventSourceType sourceType,
  String sourceTypeName,
  String sourceId,
  EventCategory category,
  String categoryName,
  String genreName,
  String title,
  LocalDate startDate,
  LocalDate endDate,
  Long venueId,
  String venueName,
  String venueAddress,
  String sidoCode,
  String sidoName,
  String sigungu,
  String posterUrl,
  List<String> introImages,
  String priceText,
  String description,
  String castMembers,
  String runtime,
  String ageLimit,
  int capacity,
  boolean onSale,
  String saleStopReason,
  LocalDateTime saleStoppedAt,
  boolean enabled,
  boolean verified,
  boolean hasSourceChange,
  LocalDateTime sourceCheckedAt,
  String updatedBy,
  LocalDateTime updatedAt
) {

  public static EventResponse of(
      Event event,
      EventVenue venue,
      boolean hasSourceChange,
      Map<String, String> sourceTypeNames,
      Map<String, String> categoryNames,
      Map<String, String> sidoNames
  ) {
    return new EventResponse(
        event.getEventId(),
        event.getSourceType(),
        sourceTypeNames.get(event.getSourceType().name()),
        event.getSourceId(),
        event.getCategory(),
        categoryNames.get(event.getCategory().name()),
        event.getGenreName(),
        event.getTitle(),
        event.getStartDate(),
        event.getEndDate(),
        event.getVenueId(),
        venue == null ? null : venue.getName(),
        venue == null ? null : venue.getAddress(),
        venue == null ? null : venue.getSidoCode(),
        venue == null ? null : sidoNames.get(venue.getSidoCode()),
        venue == null ? null : venue.getSigungu(),
        event.getPosterUrl(),
        event.getIntroImages(),
        event.getPriceText(),
        event.getDescription(),
        event.getCastMembers(),
        event.getRuntime(),
        event.getAgeLimit(),
        event.getCapacity(),
        event.isOnSale(),
        event.getSaleStopReason(),
        event.getSaleStoppedAt(),
        event.isEnabled(),
        event.isVerified(),
        hasSourceChange,
        event.getSourceCheckedAt(),
        event.getUpdatedBy(),
        event.getUpdatedAt()
    );
  }
}
