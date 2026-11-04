package com.bloomix.admin.domain.event.dto;

import com.bloomix.admin.domain.event.entity.EventSourceChangeStatus;
import jakarta.validation.constraints.NotNull;

public record EventSourceChangeStatusRequest(
  @NotNull(message = "처리 상태를 선택해주세요.")
  EventSourceChangeStatus status
) {
}
