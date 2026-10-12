package com.bloomix.admin.domain.user.entity;

import com.bloomix.admin.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

  @Id
  private UUID userId;

  // 가입 수단
  @Enumerated(EnumType.STRING)
  private UserProvider provider;

  // LOCAL: 로그인 ID, OAuth: 제공자 식별값
  private String providerId;

  // 이메일
  private String email;

  // 비밀번호
  private String password;

  // 비밀번호 연속 실패 횟수
  private int passwordFailCount;

  // 비밀번호 변경 일시
  private LocalDateTime passwordChangedAt;

  // 닉네임
  private String nickname;

  // 회원 상태
  @Enumerated(EnumType.STRING)
  private UserStatus status;

  // 최근 로그인 일시
  private LocalDateTime lastLoginAt;
}
