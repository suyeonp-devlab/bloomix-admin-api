package com.bloomix.admin.domain.batch.job.holiday;

public class HolidayApiException extends RuntimeException {

  public HolidayApiException(String message) {
    super(message);
  }

  public HolidayApiException(String message, Throwable cause) {
    super(message, cause);
  }
}
