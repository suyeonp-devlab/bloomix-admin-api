package com.bloomix.admin.domain.menu.repository;

import com.bloomix.admin.domain.menu.entity.AdminMenu;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminMenuRepository extends JpaRepository<AdminMenu, String> {

  List<AdminMenu> findAllByOrderBySortOrderAscMenuIdAsc();

  boolean existsByParentId(String parentId);

  boolean existsByMenuPathAndMenuIdNot(String menuPath, String menuId);
}
