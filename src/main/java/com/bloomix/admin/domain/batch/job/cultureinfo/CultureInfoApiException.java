package com.bloomix.admin.domain.batch.job.cultureinfo;

public class CultureInfoApiException extends RuntimeException {

  public CultureInfoApiException(String message) {
    super(message);
  }

  public CultureInfoApiException(String message, Throwable cause) {
    super(message, cause);
  }
}
