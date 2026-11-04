package com.bloomix.admin.domain.event.repository;

import static com.bloomix.admin.domain.event.entity.QEventSchedule.eventSchedule;

import com.bloomix.admin.domain.event.dto.EventScheduleListRequest;
import com.bloomix.admin.domain.event.entity.EventSchedule;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
public class EventScheduleRepositoryCustomImpl implements EventScheduleRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public Page<EventSchedule> searchSchedules(Long eventId, EventScheduleListRequest condition, Pageable pageable) {

    BooleanBuilder where = searchSchedulesCondition(eventId, condition);

    List<EventSchedule> content = queryFactory
        .selectFrom(eventSchedule)
        .where(where)
        .orderBy(eventSchedule.scheduleDate.asc(), eventSchedule.startTime.asc().nullsFirst())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    JPAQuery<Long> countQuery = queryFactory
        .select(eventSchedule.count())
        .from(eventSchedule)
        .where(where);

    return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
  }

  private BooleanBuilder searchSchedulesCondition(Long eventId, EventScheduleListRequest condition) {

    BooleanBuilder builder = new BooleanBuilder();

    // 행사
    builder.and(eventSchedule.eventId.eq(eventId));

    // 회차 날짜 범위
    if (condition.scheduleFrom() != null) {
      builder.and(eventSchedule.scheduleDate.goe(condition.scheduleFrom()));
    }

    if (condition.scheduleTo() != null) {
      builder.and(eventSchedule.scheduleDate.loe(condition.scheduleTo()));
    }

    // 사용 여부
    if (condition.enabled() != null) {
      builder.and(eventSchedule.enabled.eq(condition.enabled()));
    }

    return builder;
  }
}
