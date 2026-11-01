package com.bloomix.admin.domain.batch.job.cultureinfo;

public class CultureInfoQuotaExceededException extends RuntimeException {

  public CultureInfoQuotaExceededException(String message) {
    super(message);
  }
}
