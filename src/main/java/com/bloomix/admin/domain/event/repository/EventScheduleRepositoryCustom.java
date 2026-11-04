package com.bloomix.admin.domain.event.repository;

import com.bloomix.admin.domain.event.dto.EventScheduleListRequest;
import com.bloomix.admin.domain.event.entity.EventSchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventScheduleRepositoryCustom {

  Page<EventSchedule> searchSchedules(Long eventId, EventScheduleListRequest condition, Pageable pageable);
}
