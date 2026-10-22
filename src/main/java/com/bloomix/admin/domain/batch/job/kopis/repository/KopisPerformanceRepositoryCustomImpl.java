package com.bloomix.admin.domain.batch.job.kopis.repository;

import static com.bloomix.admin.domain.batch.job.kopis.entity.QKopisFacility.kopisFacility;
import static com.bloomix.admin.domain.batch.job.kopis.entity.QKopisPerformance.kopisPerformance;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class KopisPerformanceRepositoryCustomImpl implements KopisPerformanceRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<String> findMissingFacilityIds() {

    return queryFactory
        .select(kopisPerformance.facilityId)
        .distinct()
        .from(kopisPerformance)
        .where(
            kopisPerformance.facilityId.isNotNull(),
            JPAExpressions.selectOne()
                .from(kopisFacility)
                .where(kopisFacility.facilityId.eq(kopisPerformance.facilityId))
                .notExists()
        )
        .orderBy(kopisPerformance.facilityId.asc())
        .fetch();
  }
}
