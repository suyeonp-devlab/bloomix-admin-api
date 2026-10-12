package com.bloomix.admin.domain.user.dto;

import com.bloomix.admin.domain.user.entity.User;
import com.bloomix.admin.domain.user.entity.UserProvider;
import com.bloomix.admin.domain.user.entity.UserStatus;
import com.bloomix.admin.util.MaskingUtil;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record UserResponse(
  UUID userId,
  UserProvider provider,
  String providerName,
  String providerId,
  String email,
  String nickname,
  UserStatus status,
  String statusName,
  int passwordFailCount,
  LocalDateTime passwordChangedAt,
  LocalDateTime lastLoginAt,
  LocalDateTime joinedAt
) {

  public static UserResponse of(
      User user,
      Map<String, String> providerNames,
      Map<String, String> statusNames
  ) {
    return new UserResponse(
        user.getUserId(),
        user.getProvider(),
        providerNames.get(user.getProvider().name()),
        MaskingUtil.maskId(user.getProviderId()),
        MaskingUtil.maskEmail(user.getEmail()),
        user.getNickname(),
        user.getStatus(),
        statusNames.get(user.getStatus().name()),
        user.getPasswordFailCount(),
        user.getPasswordChangedAt(),
        user.getLastLoginAt(),
        user.getCreatedAt()
    );
  }
}
