package com.bloomix.admin.domain.code.repository;

import com.bloomix.admin.domain.code.entity.CommonCode;
import com.bloomix.admin.domain.code.entity.CommonCodeId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommonCodeRepository extends JpaRepository<CommonCode, CommonCodeId> {

  List<CommonCode> findByGroupCodeOrderBySortOrderAscCodeAsc(String groupCode);

  List<CommonCode> findByGroupCodeAndEnabledTrueOrderBySortOrderAscCodeAsc(String groupCode);
}
