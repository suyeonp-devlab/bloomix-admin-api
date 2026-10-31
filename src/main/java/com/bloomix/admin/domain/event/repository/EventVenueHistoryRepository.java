package com.bloomix.admin.domain.event.repository;

import com.bloomix.admin.domain.event.entity.EventVenueHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventVenueHistoryRepository extends JpaRepository<EventVenueHistory, Long> {
}
