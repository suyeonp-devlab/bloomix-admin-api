package com.bloomix.admin.util;

import org.springframework.util.StringUtils;

public final class MaskingUtil {

  private static final int VISIBLE_LENGTH = 3;

  private MaskingUtil() {
  }

  /** 아이디 마스킹: 앞 3자리만 노출 (3자 이하는 마지막 1자리 마스킹) */
  public static String maskId(String value) {

    if (!StringUtils.hasText(value)) return value;

    int visible = Math.min(VISIBLE_LENGTH, value.length() - 1);
    return value.substring(0, visible) + "*".repeat(value.length() - visible);
  }

  /** 이메일 마스킹: @ 앞 3자리만 노출 */
  public static String maskEmail(String email) {

    if (!StringUtils.hasText(email)) return email;

    int at = email.indexOf('@');
    if (at < 0) return maskId(email);

    return maskId(email.substring(0, at)) + email.substring(at);
  }

  /** 이름 마스킹: 첫 글자와 마지막 글자만 노출 */
  public static String maskName(String name) {

    if (!StringUtils.hasText(name)) return name;

    String value = name.trim();
    if (value.length() == 1) return "*";
    if (value.length() == 2) return value.charAt(0) + "*";

    return value.charAt(0) + "*".repeat(value.length() - 2) + value.charAt(value.length() - 1);
  }
}
