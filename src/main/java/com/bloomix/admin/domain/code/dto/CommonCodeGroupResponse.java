package com.bloomix.admin.domain.code.dto;

import com.bloomix.admin.domain.code.entity.CommonCode;
import com.bloomix.admin.domain.code.entity.CommonCodeGroup;
import java.time.LocalDateTime;
import java.util.List;

public record CommonCodeGroupResponse(
  String groupCode,
  String groupName,
  String description,
  boolean enabled,
  LocalDateTime createdAt,
  LocalDateTime updatedAt,
  List<CommonCodeResponse> codes
) {

  public static CommonCodeGroupResponse of(CommonCodeGroup group, List<CommonCode> codes) {
    return new CommonCodeGroupResponse(
        group.getGroupCode(),
        group.getGroupName(),
        group.getDescription(),
        group.isEnabled(),
        group.getCreatedAt(),
        group.getUpdatedAt(),
        codes.stream().map(CommonCodeResponse::from).toList()
    );
  }
}
