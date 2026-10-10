package com.bloomix.admin.domain.menu.controller;

import com.bloomix.admin.domain.menu.dto.AdminMenuRequest;
import com.bloomix.admin.domain.menu.dto.AdminMenuResponse;
import com.bloomix.admin.domain.menu.service.AdminMenuService;
import com.bloomix.admin.response.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin-menus")
@RequiredArgsConstructor
public class AdminMenuController {

  private final AdminMenuService adminMenuService;

  /** 어드민 사이드바 메뉴 조회 */
  @GetMapping("/sidebar")
  public ApiResponse<List<AdminMenuResponse>> getAdminSidebarMenus() {
    return ApiResponse.success(adminMenuService.getAdminSidebarMenus());
  }

  /** 어드민 전체 메뉴 조회 (미사용 포함) */
  @GetMapping
  public ApiResponse<List<AdminMenuResponse>> getAdminMenus() {
    return ApiResponse.success(adminMenuService.getAdminMenus());
  }

  /** 메뉴 등록 */
  @PostMapping
  public ApiResponse<Void> createMenu(@Valid @RequestBody AdminMenuRequest request) {
    adminMenuService.createMenu(request);
    return ApiResponse.success(null);
  }

  /** 메뉴 수정 */
  @PutMapping("/{menuId}")
  public ApiResponse<Void> updateMenu(
      @PathVariable String menuId,
      @Valid @RequestBody AdminMenuRequest request
  ) {
    adminMenuService.updateMenu(menuId, request);
    return ApiResponse.success(null);
  }
}
