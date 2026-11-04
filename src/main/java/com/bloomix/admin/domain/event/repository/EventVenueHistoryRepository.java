package com.bloomix.admin.domain.event.repository;

import com.bloomix.admin.domain.event.entity.EventVenueHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventVenueHistoryRepository extends JpaRepository<EventVenueHistory, Long> {

  Page<EventVenueHistory> findByVenueIdOrderByHistoryIdDesc(Long venueId, Pageable pageable);
}
