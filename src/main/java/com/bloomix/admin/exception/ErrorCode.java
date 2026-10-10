package com.bloomix.admin.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

  INVALID_INPUT(HttpStatus.BAD_REQUEST, "CMN_001", "입력값이 올바르지 않습니다."),
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "CMN_002", "서비스 처리 중 오류가 발생했습니다."),
  NOT_FOUND(HttpStatus.NOT_FOUND, "CMN_003", "요청한 리소스를 찾을 수 없습니다."),
  METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "CMN_004", "지원하지 않는 요청 방식입니다."),
  UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "CMN_005", "지원하지 않는 Content-Type 입니다."),

  UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH_001", "로그인이 필요합니다."),
  FORBIDDEN(HttpStatus.FORBIDDEN, "AUTH_002", "접근 권한이 없습니다."),
  LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "AUTH_003", "아이디 또는 비밀번호가 올바르지 않습니다."),
  ACCOUNT_LOCKED(HttpStatus.FORBIDDEN, "AUTH_004", "비밀번호 5회 오류로 잠긴 계정입니다. 관리자에게 문의해주세요."),
  ACCOUNT_DISABLED(HttpStatus.FORBIDDEN, "AUTH_005", "사용할 수 없는 계정입니다."),

  CODE_GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "CODE_001", "공통코드 그룹을 찾을 수 없습니다."),
  DUPLICATE_CODE_GROUP(HttpStatus.CONFLICT, "CODE_002", "이미 존재하는 공통코드 그룹입니다."),

  MENU_NOT_FOUND(HttpStatus.NOT_FOUND, "MENU_001", "메뉴를 찾을 수 없습니다."),
  DUPLICATE_MENU_ID(HttpStatus.CONFLICT, "MENU_002", "이미 존재하는 메뉴 ID입니다."),
  DUPLICATE_MENU_PATH(HttpStatus.CONFLICT, "MENU_003", "이미 사용 중인 메뉴 경로입니다.");

  private final HttpStatus status;
  private final String code;
  private final String message;
}
