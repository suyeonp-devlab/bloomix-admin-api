package com.bloomix.admin.domain.user.dto;

import com.bloomix.admin.domain.user.entity.UserStatus;
import jakarta.validation.constraints.NotNull;

public record UserStatusRequest(
  @NotNull(message = "변경할 상태를 선택해주세요.")
  UserStatus status
) {
}
