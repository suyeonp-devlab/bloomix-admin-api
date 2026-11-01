package com.bloomix.admin.domain.event.repository;

import com.bloomix.admin.domain.event.entity.EventHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventHistoryRepository extends JpaRepository<EventHistory, Long> {
}
