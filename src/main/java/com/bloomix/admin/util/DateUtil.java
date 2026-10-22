package com.bloomix.admin.util;

import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class DateUtil {

  public static final DateTimeFormatter YYYYMMDD = DateTimeFormatter.BASIC_ISO_DATE;

  private DateUtil() {
  }

  /** yyyyMMdd 변환 */
  public static LocalDate parseYyyyMMdd(String name, String value, LocalDate defaultValue) {

    if (!StringUtils.hasText(value)) return defaultValue;

    try {
      return LocalDate.parse(value.trim(), YYYYMMDD);
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("날짜 형식 오류 (yyyyMMdd): " + name + "=" + value, e);
    }
  }
}
