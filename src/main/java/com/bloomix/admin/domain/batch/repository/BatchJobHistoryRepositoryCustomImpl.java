package com.bloomix.admin.domain.batch.repository;

import static com.bloomix.admin.domain.batch.entity.QBatchJob.batchJob;
import static com.bloomix.admin.domain.batch.entity.QBatchJobHistory.batchJobHistory;

import com.bloomix.admin.domain.batch.dto.BatchJobHistoryListRequest;
import com.bloomix.admin.domain.batch.dto.BatchJobHistoryResponse;
import com.bloomix.admin.domain.batch.entity.BatchJobHistory;
import com.bloomix.admin.domain.batch.entity.QBatchJobHistory;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
public class BatchJobHistoryRepositoryCustomImpl implements BatchJobHistoryRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public Page<BatchJobHistoryResponse> searchHistories(BatchJobHistoryListRequest condition, Pageable pageable) {

    BooleanBuilder where = searchHistoriesCondition(condition);

    List<BatchJobHistoryResponse> content = queryFactory
        .select(batchJobHistory, batchJob.jobName)
        .from(batchJobHistory)
        .join(batchJob).on(batchJob.jobId.eq(batchJobHistory.jobId))
        .where(where)
        .orderBy(batchJobHistory.startedAt.desc(), batchJobHistory.historyId.desc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch()
        .stream()
        .map(row -> BatchJobHistoryResponse.of(row.get(batchJobHistory), row.get(batchJob.jobName)))
        .toList();

    JPAQuery<Long> countQuery = queryFactory
        .select(batchJobHistory.count())
        .from(batchJobHistory)
        .join(batchJob).on(batchJob.jobId.eq(batchJobHistory.jobId))
        .where(where);

    return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
  }

  @Override
  public List<BatchJobHistory> findLatestByJobIds(List<String> jobIds) {

    if (jobIds.isEmpty()) return List.of();

    QBatchJobHistory sub = new QBatchJobHistory("sub");

    return queryFactory
        .selectFrom(batchJobHistory)
        .where(batchJobHistory.historyId.in(
            JPAExpressions
                .select(sub.historyId.max())
                .from(sub)
                .where(sub.jobId.in(jobIds))
                .groupBy(sub.jobId)))
        .fetch();
  }

  private BooleanBuilder searchHistoriesCondition(BatchJobHistoryListRequest condition) {

    BooleanBuilder builder = new BooleanBuilder();

    // 검색구분 + 키워드
    if (StringUtils.hasText(condition.keyword())) {
      switch (condition.searchType()) {
        case "jobId" -> builder.and(batchJobHistory.jobId.containsIgnoreCase(condition.keyword()));
        case "jobName" -> builder.and(batchJob.jobName.containsIgnoreCase(condition.keyword()));
        case null, default -> { }
      }
    }

    // 상태
    if (condition.status() != null) {
      builder.and(batchJobHistory.status.eq(condition.status()));
    }

    // 실행 구분
    if (condition.triggerType() != null) {
      builder.and(batchJobHistory.triggerType.eq(condition.triggerType()));
    }

    // 시작일 범위
    if (condition.startedFrom() != null) {
      builder.and(batchJobHistory.startedAt.goe(condition.startedFrom().atStartOfDay()));
    }

    if (condition.startedTo() != null) {
      builder.and(batchJobHistory.startedAt.lt(condition.startedTo().plusDays(1).atStartOfDay()));
    }

    return builder;
  }
}
