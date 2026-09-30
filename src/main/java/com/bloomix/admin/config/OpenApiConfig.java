package com.bloomix.admin.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI().info(new Info()
        .title("Bloomix Admin API")
        .description("Bloomix 어드민 백엔드 API 문서")
        .version("v1"));
  }
}
