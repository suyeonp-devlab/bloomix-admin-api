package com.bloomix.admin.domain.event.repository;

import com.bloomix.admin.domain.event.entity.EventSourceChange;
import com.bloomix.admin.domain.event.entity.EventSourceChangeStatus;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventSourceChangeRepository extends JpaRepository<EventSourceChange, Long> {

  List<EventSourceChange> findAllByEventIdInAndStatus(Collection<Long> eventIds, EventSourceChangeStatus status);

  Optional<EventSourceChange> findByEventIdAndStatus(Long eventId, EventSourceChangeStatus status);

  boolean existsByEventIdAndStatus(Long eventId, EventSourceChangeStatus status);
}
