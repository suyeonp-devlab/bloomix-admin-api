package com.bloomix.admin.domain.batch.job.kopis;

public class KopisApiException extends RuntimeException {

  public KopisApiException(String message) {
    super(message);
  }

  public KopisApiException(String message, Throwable cause) {
    super(message, cause);
  }
}
