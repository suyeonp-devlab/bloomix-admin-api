package com.bloomix.admin.domain.event.repository;

import com.bloomix.admin.domain.event.entity.EventSourceType;
import com.bloomix.admin.domain.event.entity.EventVenue;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventVenueRepository extends JpaRepository<EventVenue, Long>, EventVenueRepositoryCustom {

  List<EventVenue> findAllBySourceTypeAndSourceIdIn(EventSourceType sourceType, Collection<String> sourceIds);
}
