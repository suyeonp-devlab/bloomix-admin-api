package com.bloomix.admin.domain.batch.dto;

import java.util.Map;

public record BatchJobRunRequest(
  Map<String, String> parameters
) {
}
