package com.bloomix.admin.domain.event.dto;

import com.bloomix.admin.domain.event.entity.EventSchedule;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record EventScheduleResponse(
  Long scheduleId,
  Long eventId,
  LocalDate scheduleDate,
  LocalTime startTime,
  int capacity,
  boolean enabled,
  String updatedBy,
  LocalDateTime updatedAt
) {

  public static EventScheduleResponse from(EventSchedule schedule) {
    return new EventScheduleResponse(
        schedule.getScheduleId(),
        schedule.getEventId(),
        schedule.getScheduleDate(),
        schedule.getStartTime(),
        schedule.getCapacity(),
        schedule.isEnabled(),
        schedule.getUpdatedBy(),
        schedule.getUpdatedAt()
    );
  }
}
