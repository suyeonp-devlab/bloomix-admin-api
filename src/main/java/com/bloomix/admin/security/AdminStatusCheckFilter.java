package com.bloomix.admin.security;

import com.bloomix.admin.domain.admin.entity.AdminAccount;
import com.bloomix.admin.domain.admin.entity.AdminStatus;
import com.bloomix.admin.domain.admin.repository.AdminAccountRepository;
import com.bloomix.admin.exception.BizException;
import com.bloomix.admin.exception.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

/**
 * 로그인된 요청마다 DB 계정 상태 확인 (ACTIVE가 아니면 세션을 끊는다)
 * Filter 빈으로 등록하면 Security 체인 밖에서도 실행되므로 SecurityConfig에서 직접 생성
 */
@Slf4j
@RequiredArgsConstructor
public class AdminStatusCheckFilter extends OncePerRequestFilter {

  private final AdminAccountRepository adminAccountRepository;
  private final HandlerExceptionResolver handlerExceptionResolver;
  private final SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain
  ) throws ServletException, IOException {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null
        || !authentication.isAuthenticated()
        || authentication instanceof AnonymousAuthenticationToken) {
      filterChain.doFilter(request, response);
      return;
    }

    String adminId = authentication.getName();
    AdminStatus status;
    try {
      status = adminAccountRepository.findById(adminId).map(AdminAccount::getStatus).orElse(null);
    } catch (RuntimeException e) {
      // 계정상태 확인 불가 > fail-closed
      handlerExceptionResolver.resolveException(request, response, null, e);
      return;
    }

    if (status == AdminStatus.ACTIVE) {
      filterChain.doFilter(request, response);
      return;
    }

    log.warn("inactive admin session blocked. adminId={}, status={}", adminId, status);
    logoutHandler.logout(request, response, authentication);
    handlerExceptionResolver.resolveException(request, response, null, new BizException(toErrorCode(status)));
  }

  private ErrorCode toErrorCode(AdminStatus status) {
    if (status == AdminStatus.LOCKED) return ErrorCode.ACCOUNT_LOCKED;
    if (status == AdminStatus.DISABLED) return ErrorCode.ACCOUNT_DISABLED;
    return ErrorCode.UNAUTHORIZED;
  }
}
