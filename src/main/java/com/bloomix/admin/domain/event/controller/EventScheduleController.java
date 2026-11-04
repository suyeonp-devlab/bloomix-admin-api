package com.bloomix.admin.domain.event.controller;

import com.bloomix.admin.domain.event.dto.EventScheduleRequest;
import com.bloomix.admin.domain.event.service.EventScheduleService;
import com.bloomix.admin.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/event-schedules")
@RequiredArgsConstructor
public class EventScheduleController {

  private final EventScheduleService eventScheduleService;

  /** 회차 수정 */
  @PutMapping("/{scheduleId}")
  public ApiResponse<Void> updateSchedule(
      @PathVariable Long scheduleId,
      @Valid @RequestBody EventScheduleRequest request
  ) {
    eventScheduleService.updateSchedule(scheduleId, request);
    return ApiResponse.success(null);
  }
}
