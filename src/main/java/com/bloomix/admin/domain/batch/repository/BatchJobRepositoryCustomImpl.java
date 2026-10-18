package com.bloomix.admin.domain.batch.repository;

import static com.bloomix.admin.domain.batch.entity.QBatchJob.batchJob;

import com.bloomix.admin.domain.batch.dto.BatchJobListRequest;
import com.bloomix.admin.domain.batch.entity.BatchJob;
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
public class BatchJobRepositoryCustomImpl implements BatchJobRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public Page<BatchJob> searchJobs(BatchJobListRequest condition, Pageable pageable) {

    BooleanBuilder where = searchJobsCondition(condition);

    List<BatchJob> content = queryFactory
        .selectFrom(batchJob)
        .where(where)
        .orderBy(batchJob.createdAt.desc(), batchJob.jobId.asc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    JPAQuery<Long> countQuery = queryFactory
        .select(batchJob.count())
        .from(batchJob)
        .where(where);

    return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
  }

  private BooleanBuilder searchJobsCondition(BatchJobListRequest condition) {

    BooleanBuilder builder = new BooleanBuilder();

    // 검색구분 + 키워드
    if (StringUtils.hasText(condition.keyword())) {
      switch (condition.searchType()) {
        case "jobId" -> builder.and(batchJob.jobId.containsIgnoreCase(condition.keyword()));
        case "jobName" -> builder.and(batchJob.jobName.containsIgnoreCase(condition.keyword()));
        case null, default -> { }
      }
    }

    // 사용 여부
    if (condition.enabled() != null) {
      builder.and(batchJob.enabled.eq(condition.enabled()));
    }

    return builder;
  }
}
