package com.bloomix.admin.domain.event.repository;

import com.bloomix.admin.domain.event.dto.EventVenueListRequest;
import com.bloomix.admin.domain.event.entity.EventVenue;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventVenueRepositoryCustom {

  Page<EventVenue> searchVenues(EventVenueListRequest condition, Pageable pageable);

  List<String> findKopisFacilityIdsToSync();

  List<String> findCulturePlaceSeqsToSync();
}
