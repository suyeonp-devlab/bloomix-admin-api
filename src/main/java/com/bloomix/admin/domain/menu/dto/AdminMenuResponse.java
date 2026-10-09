package com.bloomix.admin.domain.menu.dto;

import com.bloomix.admin.domain.menu.entity.AdminMenu;
import java.time.LocalDateTime;
import java.util.List;

public record AdminMenuResponse(
  String menuId,
  String parentId,
  String menuName,
  String menuPath,
  int sortOrder,
  boolean enabled,
  LocalDateTime updatedAt,
  List<AdminMenuResponse> children
) {

  public static AdminMenuResponse of(AdminMenu menu, List<AdminMenu> children) {
    return new AdminMenuResponse(
        menu.getMenuId(),
        menu.getParentId(),
        menu.getMenuName(),
        menu.getMenuPath(),
        menu.getSortOrder(),
        menu.isEnabled(),
        menu.getUpdatedAt(),
        children.stream().map(AdminMenuResponse::from).toList()
    );
  }

  public static AdminMenuResponse from(AdminMenu menu) {
    return of(menu, List.of());
  }
}
