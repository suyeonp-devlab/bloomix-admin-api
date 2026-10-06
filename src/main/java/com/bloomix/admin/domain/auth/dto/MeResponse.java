package com.bloomix.admin.domain.auth.dto;

import com.bloomix.admin.domain.admin.entity.AdminAccount;

public record MeResponse(
  String adminId,
  String adminName
) {

  public static MeResponse from(AdminAccount admin) {
    return new MeResponse(admin.getAdminId(), admin.getAdminName());
  }
}
