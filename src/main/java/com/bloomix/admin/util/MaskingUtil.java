package com.bloomix.admin.util;

import org.springframework.util.StringUtils;

/** 개인정보 마스킹 */
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
}
