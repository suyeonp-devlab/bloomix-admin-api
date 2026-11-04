package com.bloomix.admin.domain.event.dto;

import com.bloomix.admin.domain.event.entity.EventSourceType;

public record EventVenueListRequest(
  String searchType,
  String keyword,
  EventSourceType sourceType,
  String sidoCode,
  Boolean verified
) {
}
