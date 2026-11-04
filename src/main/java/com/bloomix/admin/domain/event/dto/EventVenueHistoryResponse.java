package com.bloomix.admin.domain.event.dto;

import com.bloomix.admin.domain.event.entity.EventVenueHistory;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public record EventVenueHistoryResponse(
  Long historyId,
  String name,
  String address,
  String sidoCode,
  String sidoName,
  String sigungu,
  BigDecimal latitude,
  BigDecimal longitude,
  boolean verified,
  String changedBy,
  LocalDateTime changedAt
) {

  public static EventVenueHistoryResponse of(EventVenueHistory history, Map<String, String> sidoNames) {
    return new EventVenueHistoryResponse(
        history.getHistoryId(),
        history.getName(),
        history.getAddress(),
        history.getSidoCode(),
        sidoNames.get(history.getSidoCode()),
        history.getSigungu(),
        history.getLatitude(),
        history.getLongitude(),
        history.isVerified(),
        history.getCreatedBy(),
        history.getCreatedAt()
    );
  }
}
