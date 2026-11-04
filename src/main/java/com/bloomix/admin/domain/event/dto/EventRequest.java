package com.bloomix.admin.domain.event.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record EventRequest(
  @NotBlank(message = "제목을 입력해주세요.")
  @Size(max = 500, message = "제목은 500자 이하로 입력해주세요.")
  String title,

  @NotNull(message = "시작일을 입력해주세요.")
  LocalDate startDate,

  @NotNull(message = "종료일을 입력해주세요.")
  LocalDate endDate,

  @NotNull(message = "정원을 입력해주세요.")
  @Min(value = 1, message = "정원은 1명 이상으로 입력해주세요.")
  Integer capacity,

  @NotNull(message = "노출 여부를 입력해주세요.")
  Boolean enabled,

  @NotNull(message = "확인 여부를 입력해주세요.")
  Boolean verified
) {
}
