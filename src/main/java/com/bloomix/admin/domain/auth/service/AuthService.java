package com.bloomix.admin.domain.auth.service;

import com.bloomix.admin.domain.admin.entity.AdminAccount;
import com.bloomix.admin.domain.admin.repository.AdminAccountRepository;
import com.bloomix.admin.domain.admin.entity.AdminStatus;
import com.bloomix.admin.domain.auth.dto.LoginResponse;
import com.bloomix.admin.domain.auth.dto.MeResponse;
import com.bloomix.admin.exception.BizException;
import com.bloomix.admin.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthService {

  private final AdminAccountRepository adminAccountRepository;
  private final PasswordEncoder passwordEncoder;
  private final String dummyPasswordHash;

  public AuthService(AdminAccountRepository adminAccountRepository, PasswordEncoder passwordEncoder) {
    this.adminAccountRepository = adminAccountRepository;
    this.passwordEncoder = passwordEncoder;
    this.dummyPasswordHash = passwordEncoder.encode(UUID.randomUUID().toString());
  }

  /** 로그인 */
  @Transactional(noRollbackFor = BizException.class)
  public LoginResponse login(String adminId, String password) {

    AdminAccount admin = adminAccountRepository.findWithLockByAdminId(adminId).orElse(null);

    // 아이디 미존재: 해시 비교 시간을 소모해 응답 시간으로 아이디 존재 여부를 알 수 없게 한다
    if (admin == null) {
      passwordEncoder.matches(password, dummyPasswordHash);
      throw new BizException(ErrorCode.LOGIN_FAILED);
    }

    // 계정 상태 확인
    if (admin.getStatus() == AdminStatus.LOCKED) {
      throw new BizException(ErrorCode.ACCOUNT_LOCKED);
    }

    if (admin.getStatus() == AdminStatus.DISABLED) {
      throw new BizException(ErrorCode.ACCOUNT_DISABLED);
    }

    // 비밀번호 불일치
    if (!passwordEncoder.matches(password, admin.getPassword())) {
      admin.failedPassword();
      if (admin.getStatus() == AdminStatus.LOCKED) {
        throw new BizException(ErrorCode.ACCOUNT_LOCKED);
      }
      throw new BizException(ErrorCode.LOGIN_FAILED);
    }

    admin.succeededLogin(LocalDateTime.now());
    return LoginResponse.from(admin);
  }

  /** 내 정보 조회 */
  public MeResponse getMe(String adminId) {
    AdminAccount admin = adminAccountRepository.findById(adminId)
        .orElseThrow(() -> new BizException(ErrorCode.UNAUTHORIZED));
    return MeResponse.from(admin);
  }
}
