package com.bloomix.admin.config;

import com.bloomix.admin.domain.admin.repository.AdminAccountRepository;
import com.bloomix.admin.security.AdminStatusCheckFilter;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
public class SecurityConfig {

  private final HandlerExceptionResolver handlerExceptionResolver;

  public SecurityConfig(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver) {
    this.handlerExceptionResolver = handlerExceptionResolver;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(
      HttpSecurity http,
      SecurityContextRepository securityContextRepository,
      AdminAccountRepository adminAccountRepository
  ) {

    CookieCsrfTokenRepository csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
    csrfTokenRepository.setCookieCustomizer(cookie -> cookie.sameSite("Strict"));

    return http.csrf(csrf -> csrf.spa().csrfTokenRepository(csrfTokenRepository))
        .cors(Customizer.withDefaults())
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .logout(AbstractHttpConfigurer::disable)
        .securityContext(context -> context.securityContextRepository(securityContextRepository))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.GET, "/api/auth/csrf").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
            .requestMatchers("/actuator/health", "/actuator/info").permitAll()
            .anyRequest().authenticated())
        .addFilterBefore(
            new AdminStatusCheckFilter(adminAccountRepository, handlerExceptionResolver),
            AuthorizationFilter.class)
        // 401/403도 GlobalExceptionHandler에서 ApiResponse 형식으로 응답한다.
        .exceptionHandling(exception -> exception
            .authenticationEntryPoint((request, response, e) ->
                handlerExceptionResolver.resolveException(request, response, null, e))
            .accessDeniedHandler((request, response, e) ->
                handlerExceptionResolver.resolveException(request, response, null, e)))
        .build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource(
      @Value("${app.cors.allowed-origins:}") List<String> allowedOrigins
  ) {

    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(allowedOrigins.stream().filter(StringUtils::hasText).toList());
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("Content-Type", "X-XSRF-TOKEN"));
    config.setAllowCredentials(true);
    config.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/api/**", config);
    return source;
  }

  @Bean
  public SecurityContextRepository securityContextRepository() {
    return new HttpSessionSecurityContextRepository();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
