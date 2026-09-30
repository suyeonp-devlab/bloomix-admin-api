package com.bloomix.admin.response;

/** api 공통 응답 */
public record ApiResponse<T>(
    boolean success,
    String code,
    String message,
    T data
) {

  public static <T> ApiResponse<T> success(T data) {
    return new ApiResponse<>(true, "SUCCESS", null, data);
  }

  public static <T> ApiResponse<T> fail(String code, String message) {
    return new ApiResponse<>(false, code, message, null);
  }
}
