package com.bloomix.admin.domain.admin.entity;

import com.bloomix.admin.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminAccount extends BaseEntity {

  private static final int MAX_PW_FAIL_COUNT = 5;

  @Id
  private String adminId;

  // 어드민 이름
  private String adminName;

  // 비밀번호
  private String password;

  // 초기 비밀번호 여부
  private boolean pwInit;

  // 로그인 실패 횟수
  private int pwFailCount;

  // 어드민 상태
  @Enumerated(EnumType.STRING)
  private AdminStatus status;

  // 최근 로그인 일시
  private LocalDateTime lastLoginAt;

  // 비밀번호 불일치
  public void failedPassword() {
    this.pwFailCount++;
    if (this.pwFailCount >= MAX_PW_FAIL_COUNT) {
      this.status = AdminStatus.LOCKED;
    }
  }

  // 로그인 성공
  public void succeededLogin(LocalDateTime loginAt) {
    this.pwFailCount = 0;
    this.lastLoginAt = loginAt;
  }
}
