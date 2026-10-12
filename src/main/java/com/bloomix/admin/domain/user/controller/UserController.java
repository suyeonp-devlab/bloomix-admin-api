package com.bloomix.admin.domain.user.controller;

import com.bloomix.admin.domain.user.dto.UserListRequest;
import com.bloomix.admin.domain.user.dto.UserResponse;
import com.bloomix.admin.domain.user.dto.UserStatusRequest;
import com.bloomix.admin.domain.user.service.UserService;
import com.bloomix.admin.response.ApiResponse;
import com.bloomix.admin.response.PageResponse;
import com.bloomix.admin.security.CurrentAdminId;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  /** 회원 목록 조회 */
  @GetMapping
  public ApiResponse<PageResponse<UserResponse>> searchUsers(
      @ParameterObject UserListRequest condition,
      @ParameterObject @PageableDefault(size = 20) Pageable pageable
  ) {
    return ApiResponse.success(userService.searchUsers(condition, pageable));
  }

  /** 회원 상세 조회 */
  @GetMapping("/{userId}")
  public ApiResponse<UserResponse> getUser(@PathVariable UUID userId) {
    return ApiResponse.success(userService.getUser(userId));
  }

  /** 회원 상태 변경 */
  @PutMapping("/{userId}/status")
  public ApiResponse<Void> updateStatus(
      @PathVariable UUID userId,
      @Valid @RequestBody UserStatusRequest request,
      @CurrentAdminId String adminId
  ) {
    userService.updateStatus(userId, request, adminId);
    return ApiResponse.success(null);
  }
}
