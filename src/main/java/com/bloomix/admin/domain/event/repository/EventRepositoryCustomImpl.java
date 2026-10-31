package com.bloomix.admin.domain.event.repository;

import static com.bloomix.admin.domain.batch.job.cultureinfo.entity.QCultureInfo.cultureInfo;
import static com.bloomix.admin.domain.batch.job.kopis.entity.QKopisPerformance.kopisPerformance;
import static com.bloomix.admin.domain.event.entity.QEvent.event;

import com.bloomix.admin.domain.event.entity.EventSourceType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EventRepositoryCustomImpl implements EventRepositoryCustom {

  private final JPAQueryFactory queryFactory;

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
}
