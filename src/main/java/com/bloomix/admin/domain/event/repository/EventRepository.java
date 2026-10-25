package com.bloomix.admin.domain.event.repository;

import com.bloomix.admin.domain.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
