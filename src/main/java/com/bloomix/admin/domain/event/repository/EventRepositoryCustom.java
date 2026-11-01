package com.bloomix.admin.domain.event.repository;

import java.time.LocalDate;
import java.util.List;

public interface EventRepositoryCustom {

  List<String> findKopisPerformanceIdsToSync();

  List<String> findCultureInfoSeqsToSync();

  List<Long> findEventIdsToGenerateSchedule(LocalDate from, LocalDate to);
}
