package com.bloomix.admin.domain.event.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import org.springframework.util.StringUtils;

public record EventVenueRequest(
  @NotBlank(message = "장소명을 입력해주세요.")
  @Size(max = 300, message = "장소명은 300자 이하로 입력해주세요.")
  String name,

  @Size(max = 500, message = "주소는 500자 이하로 입력해주세요.")
  String address,

  @Size(max = 50, message = "시도는 50자 이하로 입력해주세요.")
  String sidoCode,

  @Size(max = 50, message = "시군구는 50자 이하로 입력해주세요.")
  String sigungu,

  @DecimalMin(value = "-90", message = "위도는 -90 ~ 90 사이로 입력해주세요.")
  @DecimalMax(value = "90", message = "위도는 -90 ~ 90 사이로 입력해주세요.")
  BigDecimal latitude,

  @DecimalMin(value = "-180", message = "경도는 -180 ~ 180 사이로 입력해주세요.")
  @DecimalMax(value = "180", message = "경도는 -180 ~ 180 사이로 입력해주세요.")
  BigDecimal longitude,

  @NotNull(message = "확인 여부를 입력해주세요.")
  Boolean verified
) {

  // 빈 문자열 null 처리
  public EventVenueRequest {
    address = StringUtils.hasText(address) ? address : null;
    sidoCode = StringUtils.hasText(sidoCode) ? sidoCode : null;
    sigungu = StringUtils.hasText(sigungu) ? sigungu : null;
  }
}
