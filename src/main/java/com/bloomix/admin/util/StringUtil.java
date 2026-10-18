package com.bloomix.admin.util;

public final class StringUtil {

  private StringUtil() {
  }

  /** 최대 길이로 자르기 */
  public static String truncate(String value, int maxLength) {
    if (value == null || value.length() <= maxLength) return value;
    return value.substring(0, maxLength);
  }
}
