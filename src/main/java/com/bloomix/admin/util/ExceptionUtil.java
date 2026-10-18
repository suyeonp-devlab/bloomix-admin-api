package com.bloomix.admin.util;

public final class ExceptionUtil {

  private ExceptionUtil() {
  }

  /** 예외 요약 (클래스명: 메시지) */
  public static String summary(Throwable e) {
    if (e == null) return null;
    String message = e.getMessage();
    return e.getClass().getSimpleName() + (message == null ? "" : ": " + message);
  }
}
