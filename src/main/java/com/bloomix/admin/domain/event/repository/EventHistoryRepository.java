package com.bloomix.admin.domain.event.repository;

import com.bloomix.admin.domain.event.entity.EventHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventHistoryRepository extends JpaRepository<EventHistory, Long> {

  Page<EventHistory> findByEventIdOrderByHistoryIdDesc(Long eventId, Pageable pageable);
}
