package com.bloomix.admin.domain.batch.job.kopis;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import org.springframework.util.StringUtils;

/** KOPIS 연동 응답 값 변환 */
public final class KopisValueParser {

  private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy.MM.dd");
  private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  private KopisValueParser() {
  }

  /** 문자 변환 */
  public static String text(String value) {
    return StringUtils.hasText(value) ? value.trim() : null;
  }

  /** boolean 변환 */
  public static boolean yn(String value) {
    return "Y".equalsIgnoreCase(text(value));
  }

  /** 일자 변환 */
  public static LocalDate date(String value) {
    String text = text(value);
    if (text == null) return null;
    try {
      return LocalDate.parse(text, DATE);
    } catch (DateTimeParseException e) {
      return null;
    }
  }

  /** 일시 변환 */
  public static LocalDateTime dateTime(String value) {
    String text = text(value);
    if (text == null) return null;
    try {
      return LocalDateTime.parse(text, DATE_TIME);
    } catch (DateTimeParseException e) {
      return null;
    }
  }

  /** 정수 변환 */
  public static Integer integer(String value) {
    String text = text(value);
    if (text == null) return null;
    try {
      return Integer.valueOf(text.replace(",", ""));
    } catch (NumberFormatException e) {
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
