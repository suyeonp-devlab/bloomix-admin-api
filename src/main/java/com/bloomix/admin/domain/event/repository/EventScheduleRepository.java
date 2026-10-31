package com.bloomix.admin.domain.event.repository;

import com.bloomix.admin.domain.event.entity.EventSchedule;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventScheduleRepository extends JpaRepository<EventSchedule, Long> {

  List<EventSchedule> findAllByEventIdInAndScheduleDateBetween(Collection<Long> eventIds, LocalDate from, LocalDate to);
}
