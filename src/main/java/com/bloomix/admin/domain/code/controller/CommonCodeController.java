package com.bloomix.admin.domain.code.controller;

import com.bloomix.admin.domain.code.dto.CommonCodeResponse;
import com.bloomix.admin.domain.code.service.CommonCodeService;
import com.bloomix.admin.response.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/common-codes")
@RequiredArgsConstructor
public class CommonCodeController {

  private final CommonCodeService commonCodeService;

  /** 셀렉트박스용 코드 조회 */
  @GetMapping("/{groupCode}")
  public ApiResponse<List<CommonCodeResponse>> getOptions(@PathVariable String groupCode) {
    return ApiResponse.success(commonCodeService.getOptions(groupCode));
  }
}
