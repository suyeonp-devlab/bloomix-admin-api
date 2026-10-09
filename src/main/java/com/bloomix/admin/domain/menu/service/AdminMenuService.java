package com.bloomix.admin.domain.menu.service;

import com.bloomix.admin.domain.menu.dto.AdminMenuRequest;
import com.bloomix.admin.domain.menu.dto.AdminMenuResponse;
import com.bloomix.admin.domain.menu.entity.AdminMenu;
import com.bloomix.admin.domain.menu.repository.AdminMenuRepository;
import com.bloomix.admin.exception.BizException;
import com.bloomix.admin.exception.ErrorCode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminMenuService {

  private final AdminMenuRepository adminMenuRepository;

  /** 어드민 사이드바 메뉴 조회 */
  public List<AdminMenuResponse> getAdminSidebarMenus() {

    List<AdminMenu> menus = adminMenuRepository.findAllByOrderBySortOrderAscMenuIdAsc().stream()
        .filter(AdminMenu::isEnabled)
        .toList();

    // 하위 메뉴 그룹핑
    Map<String, List<AdminMenu>> childrenByParent = groupChildren(menus);

    return menus.stream()
        .filter(AdminMenu::isTopLevel)
        .map(top -> AdminMenuResponse.of(top, childrenByParent.getOrDefault(top.getMenuId(), List.of())))
        .toList();
  }

  /** 어드민 전체 메뉴 조회 (미사용 포함) */
  public List<AdminMenuResponse> getAdminMenus() {

    List<AdminMenu> menus = adminMenuRepository.findAllByOrderBySortOrderAscMenuIdAsc();

    // 하위 메뉴 그룹핑
    Map<String, List<AdminMenu>> childrenByParent = groupChildren(menus);

    return menus.stream()
        .filter(AdminMenu::isTopLevel)
        .map(top -> AdminMenuResponse.of(top, childrenByParent.getOrDefault(top.getMenuId(), List.of())))
        .toList();
  }

  /** 메뉴 등록 */
  @Transactional
  public void createMenu(AdminMenuRequest request) {

    if (adminMenuRepository.existsById(request.menuId())) {
      throw new BizException(ErrorCode.DUPLICATE_MENU_ID);
    }

    validateMenu(request.menuId(), request);

    adminMenuRepository.save(AdminMenu.create(request));
  }

  /** 메뉴 수정 */
  @Transactional
  public void updateMenu(String menuId, AdminMenuRequest request) {

    AdminMenu menu = adminMenuRepository.findById(menuId)
        .orElseThrow(() -> new BizException(ErrorCode.MENU_NOT_FOUND));

    validateMenu(menuId, request);

    menu.update(request);
  }

  /** 메뉴 등록/수정 request 검사 */
  private void validateMenu(String menuId, AdminMenuRequest request) {

    String parentId = request.parentId();
    String menuPath = request.menuPath();

    // 하위 메뉴 등록/수정 검사
    if (parentId != null) {

      if (parentId.equals(menuId)) {
        throw new BizException(ErrorCode.INVALID_INPUT, "자기 자신을 상위 메뉴로 지정할 수 없습니다.");
      }

      if (menuPath == null) {
        throw new BizException(ErrorCode.INVALID_INPUT, "하위 메뉴는 메뉴 경로를 입력해주세요.");
      }

      AdminMenu parent = adminMenuRepository.findById(parentId)
          .orElseThrow(() -> new BizException(ErrorCode.MENU_NOT_FOUND, "상위 메뉴를 찾을 수 없습니다."));

      // 최대 2depth
      if (!parent.isTopLevel()) {
        throw new BizException(ErrorCode.INVALID_INPUT, "하위 메뉴 아래에는 메뉴를 추가할 수 없습니다.");
      }

      // 하위 메뉴가 있는 메뉴는 이동 불가
      if (adminMenuRepository.existsByParentId(menuId)) {
        throw new BizException(ErrorCode.INVALID_INPUT, "하위 메뉴가 있는 메뉴는 다른 메뉴 아래로 옮길 수 없습니다.");
      }
    }

    // 화면 경로 중복 검사
    if (menuPath != null && adminMenuRepository.existsByMenuPathAndMenuIdNot(menuPath, menuId)) {
      throw new BizException(ErrorCode.DUPLICATE_MENU_PATH);
    }
  }

  /** 상위 메뉴 ID별 하위 메뉴 그룹핑 */
  private Map<String, List<AdminMenu>> groupChildren(List<AdminMenu> menus) {
    return menus.stream()
        .filter(menu -> !menu.isTopLevel())
        .collect(Collectors.groupingBy(AdminMenu::getParentId));
  }
}
