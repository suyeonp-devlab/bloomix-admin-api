package com.bloomix.admin.domain.event.repository;

import static com.bloomix.admin.domain.batch.job.cultureinfo.entity.QCultureInfo.cultureInfo;
import static com.bloomix.admin.domain.batch.job.kopis.entity.QKopisPerformance.kopisPerformance;
import static com.bloomix.admin.domain.event.entity.QEvent.event;
import static com.bloomix.admin.domain.event.entity.QEventSchedule.eventSchedule;
import static com.bloomix.admin.domain.event.entity.QEventSourceChange.eventSourceChange;
import static com.bloomix.admin.domain.event.entity.QEventVenue.eventVenue;

import com.bloomix.admin.domain.event.dto.EventListRequest;
import com.bloomix.admin.domain.event.entity.Event;
import com.bloomix.admin.domain.event.entity.EventSourceChangeStatus;
import com.bloomix.admin.domain.event.entity.EventSourceType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
public class EventRepositoryCustomImpl implements EventRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public Page<Event> searchEvents(EventListRequest condition, Pageable pageable) {

    BooleanBuilder where = searchEventsCondition(condition);

    List<Event> content = queryFactory
        .selectFrom(event)
        .leftJoin(eventVenue).on(eventVenue.venueId.eq(event.venueId))
        .where(where)
        .orderBy(event.eventId.desc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    JPAQuery<Long> countQuery = queryFactory
        .select(event.count())
        .from(event)
        .leftJoin(eventVenue).on(eventVenue.venueId.eq(event.venueId))
        .where(where);

    return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
  }

  @Override
  public List<String> findKopisPerformanceIdsToSync() {

    // 미반영 또는 변경 건
    return queryFactory
        .select(kopisPerformance.performanceId)
        .from(kopisPerformance)
        .leftJoin(event).on(
            event.sourceType.eq(EventSourceType.KOPIS),
            event.sourceId.eq(kopisPerformance.performanceId)
        )
        .where(event.eventId.isNull().or(kopisPerformance.updatedAt.gt(event.sourceCheckedAt)))
        .orderBy(kopisPerformance.performanceId.asc())
        .fetch();
  }

  @Override
  public List<String> findCultureInfoSeqsToSync() {

    // 미반영 또는 변경 건
    // 서비스구분 A(공연/전시)는 전시만 반영 (공연은 KOPIS 데이터 사용)
    return queryFactory
        .select(cultureInfo.seq)
        .from(cultureInfo)
        .leftJoin(event).on(
            event.sourceType.eq(EventSourceType.CULTURE_INFO),
            event.sourceId.eq(cultureInfo.seq)
        )
        .where(
            cultureInfo.serviceType.eq("A").and(cultureInfo.realmName.eq("전시"))
                .or(cultureInfo.serviceType.in("B", "C")),
            event.eventId.isNull().or(cultureInfo.updatedAt.gt(event.sourceCheckedAt))
        )
        .orderBy(cultureInfo.seq.asc())
        .fetch();
  }

  @Override
  public List<Long> findEventIdsToGenerateSchedule(LocalDate from, LocalDate to) {

    return queryFactory
        .select(event.eventId)
        .from(event)
        .where(
            event.enabled.isTrue(),
            event.onSale.isTrue(),
            event.verified.isTrue(),
            event.startDate.loe(to),
            event.endDate.goe(from)
        )
        .orderBy(event.eventId.asc())
        .fetch();
  }

  private BooleanBuilder searchEventsCondition(EventListRequest condition) {

    BooleanBuilder builder = new BooleanBuilder();

    // 검색구분 + 키워드
    if (StringUtils.hasText(condition.keyword())) {
      switch (condition.searchType()) {
        case "TITLE" -> builder.and(event.title.containsIgnoreCase(condition.keyword()));
        case "VENUE" -> builder.and(eventVenue.name.containsIgnoreCase(condition.keyword()));
        case "SOURCE_ID" -> builder.and(event.sourceId.eq(condition.keyword()));
        case null, default -> { }
      }
    }

    // 카테고리
    if (condition.category() != null) {
      builder.and(event.category.eq(condition.category()));
    }

    // 출처
    if (condition.sourceType() != null) {
      builder.and(event.sourceType.eq(condition.sourceType()));
    }

    // 시도
    if (StringUtils.hasText(condition.sidoCode())) {
      builder.and(eventVenue.sidoCode.eq(condition.sidoCode()));
    }

    // 기간
    if (condition.periodFrom() != null) {
      builder.and(event.endDate.goe(condition.periodFrom()));
    }

    if (condition.periodTo() != null) {
      builder.and(event.startDate.loe(condition.periodTo()));
    }

    // 노출 여부
    if (condition.enabled() != null) {
      builder.and(event.enabled.eq(condition.enabled()));
    }

    // 확인 여부
    if (condition.verified() != null) {
      builder.and(event.verified.eq(condition.verified()));
    }

    // 판매 여부
    if (condition.onSale() != null) {
      builder.and(event.onSale.eq(condition.onSale()));
    }

    // 회차 여부
    if (condition.hasSchedule() != null) {
      BooleanExpression exists = JPAExpressions
          .selectOne()
          .from(eventSchedule)
          .where(eventSchedule.eventId.eq(event.eventId))
          .exists();
      builder.and(condition.hasSchedule() ? exists : exists.not());
    }

    // 원본 변경 후보 여부
    if (condition.hasSourceChange() != null) {
      BooleanExpression exists = JPAExpressions
          .selectOne()
          .from(eventSourceChange)
          .where(
              eventSourceChange.eventId.eq(event.eventId),
              eventSourceChange.status.eq(EventSourceChangeStatus.PENDING)
          )
          .exists();
      builder.and(condition.hasSourceChange() ? exists : exists.not());
    }

    return builder;
  }
}
