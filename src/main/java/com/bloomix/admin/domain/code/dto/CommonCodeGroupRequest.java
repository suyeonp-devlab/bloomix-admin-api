package com.bloomix.admin.domain.code.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CommonCodeGroupRequest(
  @NotBlank(message = "그룹코드를 입력해주세요.")
  @Size(max = 50, message = "그룹코드는 50자 이하로 입력해주세요.")
  @Pattern(regexp = "^[A-Z_]+$", message = "그룹코드는 영문 대문자와 _만 사용할 수 있습니다.")
  String groupCode,

  @NotBlank(message = "그룹명을 입력해주세요.")
  @Size(max = 100, message = "그룹명은 100자 이하로 입력해주세요.")
  String groupName,

  @Size(max = 255, message = "설명은 255자 이하로 입력해주세요.")
  String description,

  @NotNull(message = "그룹 사용 여부를 입력해주세요.")
  Boolean enabled,

  @NotNull(message = "코드 목록을 입력해주세요.")
  @Valid
  List<CommonCodeRequest> codes
) {
}
