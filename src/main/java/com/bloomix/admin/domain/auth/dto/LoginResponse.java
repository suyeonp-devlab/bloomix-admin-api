package com.bloomix.admin.domain.auth.dto;

import com.bloomix.admin.domain.admin.entity.AdminAccount;

public record LoginResponse(
  String adminId,
  String adminName
) {

  public static LoginResponse from(AdminAccount admin) {
    return new LoginResponse(admin.getAdminId(), admin.getAdminName());
  }
}
