package com.bloomix.admin.domain.user.dto;

import com.bloomix.admin.domain.user.entity.User;
import com.bloomix.admin.domain.user.entity.UserProvider;
import com.bloomix.admin.domain.user.entity.UserStatus;
import com.bloomix.admin.util.MaskingUtil;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
  UUID userId,
  UserProvider provider,
  String providerId,
  String email,
  String nickname,
  UserStatus status,
  int passwordFailCount,
  LocalDateTime passwordChangedAt,
  LocalDateTime lastLoginAt
) {

  public static UserResponse from(User user) {
    return new UserResponse(
        user.getUserId(),
        user.getProvider(),
        MaskingUtil.maskId(user.getProviderId()),
        MaskingUtil.maskEmail(user.getEmail()),
        user.getNickname(),
        user.getStatus(),
        user.getPasswordFailCount(),
        user.getPasswordChangedAt(),
        user.getLastLoginAt()
    );
  }
}
