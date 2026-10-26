package com.bloomix.admin.domain.event.repository;

import java.util.List;

public interface EventRepositoryCustom {

  List<String> findKopisPerformanceIdsToSync();

  List<String> findCultureInfoSeqsToSync();
}
