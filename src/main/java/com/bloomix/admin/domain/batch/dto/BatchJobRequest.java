package com.bloomix.admin.domain.batch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.util.StringUtils;

public record BatchJobRequest(
  @NotBlank(message = "배치 ID를 입력해주세요.")
  @Size(max = 50, message = "배치 ID는 50자 이하로 입력해주세요.")
  @Pattern(regexp = "^[A-Z0-9_]+$", message = "배치 ID는 영문 대문자, 숫자, _만 사용할 수 있습니다.")
  String jobId,

  @NotBlank(message = "배치명을 입력해주세요.")
  @Size(max = 100, message = "배치명은 100자 이하로 입력해주세요.")
  String jobName,

  @Size(max = 255, message = "설명은 255자 이하로 입력해주세요.")
  String description,

  @Size(max = 50, message = "실행 주기는 50자 이하로 입력해주세요.")
  String cronExpression,

  @NotNull(message = "사용 여부를 입력해주세요.")
  Boolean enabled
) {

  // 빈 문자열 null 처리
  public BatchJobRequest {
    description = StringUtils.hasText(description) ? description : null;
    cronExpression = StringUtils.hasText(cronExpression) ? cronExpression : null;
  }
}
