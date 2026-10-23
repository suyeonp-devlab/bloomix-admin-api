package com.bloomix.admin.domain.batch.job.cultureinfo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import org.springframework.util.StringUtils;

/** 문화정보원 연동 응답 값 변환 */
public final class CultureInfoValueParser {

  private static final DateTimeFormatter YYYYMMDD = DateTimeFormatter.BASIC_ISO_DATE;

  private CultureInfoValueParser() {
  }

  /** 문자 변환 */
  public static String text(String value) {
    return StringUtils.hasText(value) ? value.trim() : null;
  }

  /** 일자 변환 */
  public static LocalDate date(String value) {
    String text = text(value);
    if (text == null) return null;

    String digits = text.replaceAll("\\D", "");
    if (digits.length() != 8) return null;

    try {
      return LocalDate.parse(digits, YYYYMMDD);
    } catch (DateTimeParseException e) {
      return null;
    }
  }

  /** 소수 변환 */
  public static BigDecimal decimal(String value) {
    String text = text(value);
    if (text == null) return null;
    try {
      return new BigDecimal(text.replace(",", ""));
    } catch (NumberFormatException e) {
      return null;
    }
  }
}
