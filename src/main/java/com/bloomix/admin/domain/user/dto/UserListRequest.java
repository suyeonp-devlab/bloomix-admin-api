package com.bloomix.admin.domain.user.dto;

import com.bloomix.admin.domain.user.entity.UserProvider;
import com.bloomix.admin.domain.user.entity.UserStatus;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public record UserListRequest(
  String searchType,
  String keyword,
  UserProvider provider,
  UserStatus status,
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate joinedFrom,
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate joinedTo
) {
}
