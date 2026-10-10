package com.bloomix.admin.domain.menu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.util.StringUtils;

public record AdminMenuRequest(
  @NotBlank(message = "메뉴 ID를 입력해주세요.")
  @Size(max = 50, message = "메뉴 ID는 50자 이하로 입력해주세요.")
  @Pattern(regexp = "^[A-Z_]+$", message = "메뉴 ID는 영문 대문자와 _만 사용할 수 있습니다.")
  String menuId,

  String parentId,

  @NotBlank(message = "메뉴명을 입력해주세요.")
  @Size(max = 50, message = "메뉴명은 50자 이하로 입력해주세요.")
  String menuName,

  @Size(max = 200, message = "메뉴 경로는 200자 이하로 입력해주세요.")
  @Pattern(regexp = "^/.*", message = "메뉴 경로는 /로 시작해야 합니다.")
  String menuPath,

  @NotNull(message = "정렬 순서를 입력해주세요.")
  Integer sortOrder,

  @NotNull(message = "사용 여부를 입력해주세요.")
  Boolean enabled
) {

  // 빈 문자열 null 처리
  public AdminMenuRequest {
    parentId = StringUtils.hasText(parentId) ? parentId : null;
    menuPath = StringUtils.hasText(menuPath) ? menuPath : null;
  }
}
