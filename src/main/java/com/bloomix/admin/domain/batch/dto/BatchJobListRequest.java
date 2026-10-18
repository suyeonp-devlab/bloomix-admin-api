package com.bloomix.admin.domain.batch.dto;

public record BatchJobListRequest(
  String searchType,
  String keyword,
  Boolean enabled
) {
}
