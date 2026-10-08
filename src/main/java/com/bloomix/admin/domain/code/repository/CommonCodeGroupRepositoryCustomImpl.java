package com.bloomix.admin.domain.code.repository;

import static com.bloomix.admin.domain.code.entity.QCommonCodeGroup.commonCodeGroup;

import com.bloomix.admin.domain.code.dto.CommonCodeGroupListRequest;
import com.bloomix.admin.domain.code.entity.CommonCodeGroup;
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
public class CommonCodeGroupRepositoryCustomImpl implements CommonCodeGroupRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public Page<CommonCodeGroup> searchGroups(CommonCodeGroupListRequest condition, Pageable pageable) {

    BooleanBuilder where = searchGroupsCondition(condition);

    List<CommonCodeGroup> content = queryFactory
        .selectFrom(commonCodeGroup)
        .where(where)
        .orderBy(commonCodeGroup.createdAt.desc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    JPAQuery<Long> countQuery = queryFactory
        .select(commonCodeGroup.count())
        .from(commonCodeGroup)
        .where(where);

    return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
  }

  private BooleanBuilder searchGroupsCondition(CommonCodeGroupListRequest condition) {

    BooleanBuilder builder = new BooleanBuilder();

    // 검색구분 + 키워드
    if (StringUtils.hasText(condition.keyword())) {
      switch (condition.searchType()) {
        case "groupCode" -> builder.and(commonCodeGroup.groupCode.containsIgnoreCase(condition.keyword()));
        case "groupName" -> builder.and(commonCodeGroup.groupName.containsIgnoreCase(condition.keyword()));
        case null, default -> { }
      }
    }

    // 사용여부
    if (condition.enabled() != null) {
      builder.and(commonCodeGroup.enabled.eq(condition.enabled()));
    }

    return builder;
  }
}
