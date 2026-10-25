package com.bloomix.admin.domain.event.repository;

import com.bloomix.admin.domain.event.entity.EventVenue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventVenueRepository extends JpaRepository<EventVenue, Long> {
}
