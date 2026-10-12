package com.bloomix.admin.domain.user.service;

import com.bloomix.admin.domain.code.service.CommonCodeService;
import com.bloomix.admin.domain.user.dto.UserListRequest;
import com.bloomix.admin.domain.user.dto.UserResponse;
import com.bloomix.admin.domain.user.dto.UserStatusRequest;
import com.bloomix.admin.domain.user.entity.User;
import com.bloomix.admin.domain.user.entity.UserStatus;
import com.bloomix.admin.domain.user.repository.UserRepository;
import com.bloomix.admin.exception.BizException;
import com.bloomix.admin.exception.ErrorCode;
import com.bloomix.admin.response.PageResponse;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

  private static final String PROVIDER_GROUP_CODE = "USER_PROVIDER";
  private static final String STATUS_GROUP_CODE = "USER_STATUS";

  private final UserRepository userRepository;
  private final CommonCodeService commonCodeService;

  /** 회원 목록 조회 */
  public PageResponse<UserResponse> searchUsers(UserListRequest condition, Pageable pageable) {

    Map<String, String> providerNames = commonCodeService.getCodeNameMap(PROVIDER_GROUP_CODE);
    Map<String, String> statusNames = commonCodeService.getCodeNameMap(STATUS_GROUP_CODE);

    return PageResponse.from(userRepository.searchUsers(condition, pageable)
        .map(user -> UserResponse.of(user, providerNames, statusNames)));
  }

  /** 회원 상세 조회 */
  public UserResponse getUser(UUID userId) {

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BizException(ErrorCode.USER_NOT_FOUND));

    return UserResponse.of(user,
        commonCodeService.getCodeNameMap(PROVIDER_GROUP_CODE),
        commonCodeService.getCodeNameMap(STATUS_GROUP_CODE));
  }

  /** 회원 상태 변경 (이용정지, 정지 해제) */
  @Transactional
  public void updateStatus(UUID userId, UserStatusRequest request, String adminId) {

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BizException(ErrorCode.USER_NOT_FOUND));

    validateStatusChange(user.getStatus(), request.status());

    long updated = userRepository.updateStatus(userId, user.getStatus(), request.status(), adminId);
    if (updated == 0) {
      throw new BizException(ErrorCode.USER_STATUS_CONFLICT);
    }
  }

  /** 회원 상태 변경 가능 여부 검사 */
  private void validateStatusChange(UserStatus current, UserStatus next) {

    boolean allowed = switch (current) {
      case SUSPENDED -> next == UserStatus.ACTIVE;
      case ACTIVE -> next == UserStatus.SUSPENDED;
      case LOCKED, WITHDRAWN -> false;
    };

    if (!allowed) {
      throw new BizException(ErrorCode.INVALID_USER_STATUS_CHANGE);
    }
  }
}
