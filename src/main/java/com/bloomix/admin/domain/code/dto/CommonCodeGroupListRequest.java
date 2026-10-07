package com.bloomix.admin.domain.code.dto;

public record CommonCodeGroupListRequest(
  String searchType,
  String keyword,
  Boolean enabled
) {
}
