package com.bloomix.admin.domain.event.repository;

import com.bloomix.admin.domain.event.dto.EventListRequest;
import com.bloomix.admin.domain.event.entity.Event;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventRepositoryCustom {

  Page<Event> searchEvents(EventListRequest condition, Pageable pageable);

  List<String> findKopisPerformanceIdsToSync();

  List<String> findCultureInfoSeqsToSync();

  List<Long> findEventIdsToGenerateSchedule(LocalDate from, LocalDate to);
}
