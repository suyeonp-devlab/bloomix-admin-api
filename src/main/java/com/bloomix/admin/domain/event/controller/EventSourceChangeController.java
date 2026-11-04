package com.bloomix.admin.domain.event.controller;

import com.bloomix.admin.domain.event.dto.EventSourceChangeStatusRequest;
import com.bloomix.admin.domain.event.service.EventService;
import com.bloomix.admin.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/event-source-changes")
@RequiredArgsConstructor
public class EventSourceChangeController {

  private final EventService eventService;

  /** 원본 변경 후보 반영, 무시 */
  @PutMapping("/{changeId}/status")
  public ApiResponse<Void> updateStatus(
      @PathVariable Long changeId,
      @Valid @RequestBody EventSourceChangeStatusRequest request
  ) {
    eventService.updateSourceChangeStatus(changeId, request);
    return ApiResponse.success(null);
  }
}
