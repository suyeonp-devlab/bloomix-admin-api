package com.bloomix.admin.domain.event.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.util.StringUtils;

public record EventSaleRequest(
  @NotNull(message = "판매 여부를 입력해주세요.")
  Boolean onSale,

  @Size(max = 500, message = "판매 중단 사유는 500자 이하로 입력해주세요.")
  String saleStopReason
) {

  // 빈 문자열 null 처리
  public EventSaleRequest {
    saleStopReason = StringUtils.hasText(saleStopReason) ? saleStopReason : null;
  }
}
