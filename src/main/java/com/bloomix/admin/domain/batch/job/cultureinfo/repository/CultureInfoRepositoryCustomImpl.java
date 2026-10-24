package com.bloomix.admin.domain.batch.job.cultureinfo.repository;

import static com.bloomix.admin.domain.batch.job.cultureinfo.entity.QCultureInfo.cultureInfo;
import static com.bloomix.admin.domain.batch.job.cultureinfo.entity.QCulturePlace.culturePlace;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CultureInfoRepositoryCustomImpl implements CultureInfoRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<String> findRefreshTargetSeqs(LocalDate today, LocalDateTime syncedBefore, long limit) {

    return queryFactory
        .select(cultureInfo.seq)
        .from(cultureInfo)
        .where(
            cultureInfo.endDate.goe(today),
            cultureInfo.detailSyncedAt.lt(syncedBefore)
        )
        .orderBy(cultureInfo.detailSyncedAt.asc(), cultureInfo.seq.asc())
        .limit(limit)
        .fetch();
  }

  @Override
  public List<String> findMissingPlaceSeqs(long limit) {

    return queryFactory
        .select(cultureInfo.placeSeq)
        .distinct()
        .from(cultureInfo)
        .where(
            cultureInfo.placeSeq.isNotNull(),
            JPAExpressions.selectOne()
                .from(culturePlace)
                .where(culturePlace.placeSeq.eq(cultureInfo.placeSeq))
                .notExists()
        )
        .orderBy(cultureInfo.placeSeq.asc())
        .limit(limit)
        .fetch();
  }
}
