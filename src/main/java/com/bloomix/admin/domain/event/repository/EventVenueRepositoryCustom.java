package com.bloomix.admin.domain.event.repository;

import java.util.List;

public interface EventVenueRepositoryCustom {

  List<String> findKopisFacilityIdsToSync();

  List<String> findCulturePlaceSeqsToSync();
}
