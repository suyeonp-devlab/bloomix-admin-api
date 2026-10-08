package com.bloomix.admin.domain.code.dto;

import com.bloomix.admin.domain.code.entity.CommonCode;

public record CommonCodeResponse(
  String code,
  String codeName,
  int sortOrder,
  boolean enabled,
  String etc1,
  String etc2,
  String etc3
) {

  public static CommonCodeResponse from(CommonCode code) {
    return new CommonCodeResponse(
        code.getCode(),
        code.getCodeName(),
        code.getSortOrder(),
        code.isEnabled(),
        code.getEtc1(),
        code.getEtc2(),
        code.getEtc3()
    );
  }
}
