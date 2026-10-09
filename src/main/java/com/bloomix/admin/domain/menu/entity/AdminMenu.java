package com.bloomix.admin.domain.menu.entity;

import com.bloomix.admin.domain.BaseEntity;
import com.bloomix.admin.domain.menu.dto.AdminMenuRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminMenu extends BaseEntity {

  @Id
  private String menuId;

  // 상위 메뉴 ID
  private String parentId;

  // 메뉴명
  private String menuName;

  // 화면 경로
  private String menuPath;

  // 정렬 순서
  private int sortOrder;

  // 사용 여부
  private boolean enabled;

  // 메뉴 생성
  public static AdminMenu create(AdminMenuRequest request) {
    AdminMenu entity = new AdminMenu();
    entity.menuId = request.menuId();
    entity.update(request);
    return entity;
  }

  // 메뉴 수정
  public void update(AdminMenuRequest request) {
    this.parentId = request.parentId();
    this.menuName = request.menuName();
    this.menuPath = request.menuPath();
    this.sortOrder = request.sortOrder();
    this.enabled = request.enabled();
  }

  // 최상위 메뉴 여부
  public boolean isTopLevel() {
    return parentId == null;
  }
}
