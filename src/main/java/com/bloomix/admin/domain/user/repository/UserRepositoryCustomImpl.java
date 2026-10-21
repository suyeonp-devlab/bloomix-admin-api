package com.bloomix.admin.domain.user.repository;

import static com.bloomix.admin.domain.user.entity.QUser.user;

import com.bloomix.admin.domain.user.dto.UserListRequest;
import com.bloomix.admin.domain.user.entity.User;
import com.bloomix.admin.domain.user.entity.UserStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public Page<User> searchUsers(UserListRequest condition, Pageable pageable) {

    BooleanBuilder where = searchUsersCondition(condition);

    List<User> content = queryFactory
        .selectFrom(user)
        .where(where)
        .orderBy(user.createdAt.desc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    JPAQuery<Long> countQuery = queryFactory
        .select(user.count())
        .from(user)
        .where(where);

    return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
  }

  @Override
  public long updateStatus(UUID userId, UserStatus currentStatus, UserStatus newStatus, String adminId) {

    return queryFactory
        .update(user)
        .set(user.status, newStatus)
        .set(user.updatedBy, adminId)
        .set(user.updatedAt, LocalDateTime.now())
        .where(user.userId.eq(userId), user.status.eq(currentStatus))
        .execute();
  }

  private BooleanBuilder searchUsersCondition(UserListRequest condition) {

    BooleanBuilder builder = new BooleanBuilder();

    // 검색구분 + 키워드
    if (StringUtils.hasText(condition.keyword())) {
      switch (condition.searchType()) {
        case "PROVIDER_ID" -> builder.and(user.providerId.eq(condition.keyword()));
        case "EMAIL" -> builder.and(user.email.eq(condition.keyword()));
        case "NICKNAME" -> builder.and(user.nickname.containsIgnoreCase(condition.keyword()));
        case null, default -> { }
      }
    }

    // 가입 수단
    if (condition.provider() != null) {
      builder.and(user.provider.eq(condition.provider()));
    }

    // 상태
    if (condition.status() != null) {
      builder.and(user.status.eq(condition.status()));
    }

    // 가입일 범위
    if (condition.joinedFrom() != null) {
      builder.and(user.createdAt.goe(condition.joinedFrom().atStartOfDay()));
    }

    if (condition.joinedTo() != null) {
      builder.and(user.createdAt.lt(condition.joinedTo().plusDays(1).atStartOfDay()));
    }

    return builder;
  }
}
