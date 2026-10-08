package com.bloomix.admin.domain.code.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CommonCodeRequest(
  @NotBlank(message = "코드를 입력해주세요.")
  @Size(max = 50, message = "코드는 50자 이하로 입력해주세요.")
  @Pattern(regexp = "^[A-Z_]+$", message = "코드는 영문 대문자와 _만 사용할 수 있습니다.")
  String code,

  @NotBlank(message = "코드명을 입력해주세요.")
  @Size(max = 100, message = "코드명은 100자 이하로 입력해주세요.")
  String codeName,

  @NotNull(message = "정렬 순서를 입력해주세요.")
  Integer sortOrder,

  @NotNull(message = "코드 사용 여부를 입력해주세요.")
  Boolean enabled,

  @Size(max = 50, message = "etc1은 50자 이하로 입력해주세요.")
  String etc1,

  @Size(max = 50, message = "etc2는 50자 이하로 입력해주세요.")
  String etc2,

  @Size(max = 50, message = "etc3은 50자 이하로 입력해주세요.")
  String etc3
) {
}
