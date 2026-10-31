package com.bloomix.admin.domain.event.repository;

import static com.bloomix.admin.domain.batch.job.cultureinfo.entity.QCulturePlace.culturePlace;
import static com.bloomix.admin.domain.batch.job.kopis.entity.QKopisFacility.kopisFacility;
import static com.bloomix.admin.domain.event.entity.QEventVenue.eventVenue;

import com.bloomix.admin.domain.event.entity.EventSourceType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EventVenueRepositoryCustomImpl implements EventVenueRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<String> findKopisFacilityIdsToSync() {

    // 미반영 건
    return queryFactory
        .select(kopisFacility.facilityId)
        .from(kopisFacility)
        .leftJoin(eventVenue).on(
            eventVenue.sourceType.eq(EventSourceType.KOPIS),
            eventVenue.sourceId.eq(kopisFacility.facilityId)
        )
        .where(eventVenue.venueId.isNull())
        .orderBy(kopisFacility.facilityId.asc())
        .fetch();
  }

  @Override
  public List<String> findCulturePlaceSeqsToSync() {

    // 미반영 건
    return queryFactory
        .select(culturePlace.placeSeq)
        .from(culturePlace)
        .leftJoin(eventVenue).on(
            eventVenue.sourceType.eq(EventSourceType.CULTURE_INFO),
            eventVenue.sourceId.eq(culturePlace.placeSeq)
        )
        .where(eventVenue.venueId.isNull())
        .orderBy(culturePlace.placeSeq.asc())
        .fetch();
  }
}
