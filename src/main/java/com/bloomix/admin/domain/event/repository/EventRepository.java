package com.bloomix.admin.domain.event.repository;

import com.bloomix.admin.domain.event.entity.Event;
import com.bloomix.admin.domain.event.entity.EventSourceType;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long>, EventRepositoryCustom {

  List<Event> findAllBySourceTypeAndSourceIdIn(EventSourceType sourceType, Collection<String> sourceIds);
}
