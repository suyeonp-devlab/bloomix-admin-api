package com.bloomix.admin.domain.event.repository;

import static com.bloomix.admin.domain.batch.job.cultureinfo.entity.QCulturePlace.culturePlace;
import static com.bloomix.admin.domain.batch.job.kopis.entity.QKopisFacility.kopisFacility;
import static com.bloomix.admin.domain.event.entity.QEventVenue.eventVenue;

import com.bloomix.admin.domain.event.dto.EventVenueListRequest;
import com.bloomix.admin.domain.event.entity.EventSourceType;
import com.bloomix.admin.domain.event.entity.EventVenue;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
public class EventVenueRepositoryCustomImpl implements EventVenueRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public Page<EventVenue> searchVenues(EventVenueListRequest condition, Pageable pageable) {

    BooleanBuilder where = searchVenuesCondition(condition);

    List<EventVenue> content = queryFactory
        .selectFrom(eventVenue)
        .where(where)
        .orderBy(eventVenue.venueId.desc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    JPAQuery<Long> countQuery = queryFactory
        .select(eventVenue.count())
        .from(eventVenue)
        .where(where);

    return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
  }

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

  private BooleanBuilder searchVenuesCondition(EventVenueListRequest condition) {

    BooleanBuilder builder = new BooleanBuilder();

    // 검색구분 + 키워드
    if (StringUtils.hasText(condition.keyword())) {
      switch (condition.searchType()) {
        case "NAME" -> builder.and(eventVenue.name.containsIgnoreCase(condition.keyword()));
        case "ADDRESS" -> builder.and(eventVenue.address.containsIgnoreCase(condition.keyword()));
        case null, default -> { }
      }
    }

    // 출처
    if (condition.sourceType() != null) {
      builder.and(eventVenue.sourceType.eq(condition.sourceType()));
    }

    // 시도
    if (StringUtils.hasText(condition.sidoCode())) {
      builder.and(eventVenue.sidoCode.eq(condition.sidoCode()));
    }

    // 확인 여부
    if (condition.verified() != null) {
      builder.and(eventVenue.verified.eq(condition.verified()));
    }

    return builder;
  }
}
