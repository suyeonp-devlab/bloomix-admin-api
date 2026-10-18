package com.bloomix.admin.config;

import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaAuditingConfig {

  // 배치 스레드 접두어
  public static final String BATCH_THREAD_PREFIX = "batch-";

  // 배치 스레드 작성자
  private static final String BATCH_AUDITOR = "batch";

  @Bean
  public AuditorAware<String> auditorAware() {
    return () -> {

      // 로그인 어드민
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication != null
          && authentication.isAuthenticated()
          && !(authentication instanceof AnonymousAuthenticationToken)) {
        return Optional.of(authentication.getName());
      }

      // 배치 스레드
      if (Thread.currentThread().getName().startsWith(BATCH_THREAD_PREFIX)) {
        return Optional.of(BATCH_AUDITOR);
      }

      return Optional.empty();
    };
  }
}
