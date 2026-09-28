package com.bloomix.admin;

import org.springframework.boot.SpringApplication;

public class TestBloomixAdminApiApplication {

  public static void main(String[] args) {
    SpringApplication.from(BloomixAdminApiApplication::main).with(TestcontainersConfiguration.class).run(args);
  }

}
