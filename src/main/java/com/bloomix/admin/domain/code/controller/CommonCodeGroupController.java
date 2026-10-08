package com.bloomix.admin.domain.code.controller;

import com.bloomix.admin.domain.code.dto.CommonCodeGroupRequest;
import com.bloomix.admin.domain.code.dto.CommonCodeGroupResponse;
import com.bloomix.admin.domain.code.dto.CommonCodeGroupListResponse;
import com.bloomix.admin.domain.code.dto.CommonCodeGroupListRequest;
import com.bloomix.admin.domain.code.service.CommonCodeService;
import com.bloomix.admin.response.ApiResponse;
import com.bloomix.admin.response.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/common-code-groups")
@RequiredArgsConstructor
public class CommonCodeGroupController {

  private final CommonCodeService commonCodeService;

  /** 코드 그룹 목록 조회 */
  @GetMapping
  public ApiResponse<PageResponse<CommonCodeGroupListResponse>> searchGroups(
      @ParameterObject CommonCodeGroupListRequest condition,
      @ParameterObject @PageableDefault(size = 20) Pageable pageable
  ) {
    return ApiResponse.success(commonCodeService.searchGroups(condition, pageable));
  }

  /** 코드 그룹 상세 조회 */
  @GetMapping("/{groupCode}")
  public ApiResponse<CommonCodeGroupResponse> getGroup(@PathVariable String groupCode) {
    return ApiResponse.success(commonCodeService.getGroup(groupCode));
  }

  /** 코드 그룹 + 코드 일괄 등록 */
  @PostMapping
  public ApiResponse<Void> createGroup(@Valid @RequestBody CommonCodeGroupRequest request) {
    commonCodeService.createGroup(request);
    return ApiResponse.success(null);
  }

  /** 그룹 + 코드 일괄 수정 */
  @PutMapping("/{groupCode}")
  public ApiResponse<Void> updateGroup(
      @PathVariable String groupCode,
      @Valid @RequestBody CommonCodeGroupRequest request
  ) {
    commonCodeService.updateGroup(groupCode, request);
    return ApiResponse.success(null);
  }
}
