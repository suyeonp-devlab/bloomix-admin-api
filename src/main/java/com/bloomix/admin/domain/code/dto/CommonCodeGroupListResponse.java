package com.bloomix.admin.domain.code.dto;

import com.bloomix.admin.domain.code.entity.CommonCodeGroup;
import java.time.LocalDateTime;

public record CommonCodeGroupListResponse(
  String groupCode,
  String groupName,
  String description,
  boolean enabled,
  LocalDateTime updatedAt
) {

  public static CommonCodeGroupListResponse from(CommonCodeGroup group) {
    return new CommonCodeGroupListResponse(
        group.getGroupCode(),
        group.getGroupName(),
        group.getDescription(),
        group.isEnabled(),
        group.getUpdatedAt()
    );
  }
}
