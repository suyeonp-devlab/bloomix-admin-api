package com.bloomix.admin.domain.auth.controller;

import com.bloomix.admin.domain.auth.service.AuthService;
import com.bloomix.admin.domain.auth.dto.LoginRequest;
import com.bloomix.admin.domain.auth.dto.LoginResponse;
import com.bloomix.admin.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;
  private final SecurityContextRepository securityContextRepository;
  private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
  private final SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();

  /** XSRF-TOKEN 발급 */
  @GetMapping("/csrf")
  public ApiResponse<Void> csrf() {
    return ApiResponse.success(null);
  }

  /** 로그인 */
  @PostMapping("/login")
  public ApiResponse<LoginResponse> login(
      @Valid @RequestBody LoginRequest request,
      HttpServletRequest httpRequest,
      HttpServletResponse httpResponse
  ) {

    LoginResponse response = authService.login(request.adminId(), request.password());

    // 세션 고정 공격 방지: 로그인 시 매번 새로운 세션 ID를 쓰도록 기존 ID 교체
    if (httpRequest.getSession(false) != null) {
      httpRequest.changeSessionId();
    }

    Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(
        response.adminId(), null, AuthorityUtils.createAuthorityList("ROLE_ADMIN"));

    SecurityContext context = securityContextHolderStrategy.createEmptyContext();
    context.setAuthentication(authentication);

    securityContextHolderStrategy.setContext(context);
    securityContextRepository.saveContext(context, httpRequest, httpResponse);
    return ApiResponse.success(response);
  }

  /** 로그아웃 */
  @PostMapping("/logout")
  public ApiResponse<Void> logout(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
    logoutHandler.logout(httpRequest, httpResponse, securityContextHolderStrategy.getContext().getAuthentication());
    return ApiResponse.success(null);
  }
}
