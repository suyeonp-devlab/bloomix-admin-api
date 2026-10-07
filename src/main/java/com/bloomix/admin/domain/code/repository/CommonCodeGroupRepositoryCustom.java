package com.bloomix.admin.domain.code.repository;

import com.bloomix.admin.domain.code.dto.CommonCodeGroupListRequest;
import com.bloomix.admin.domain.code.entity.CommonCodeGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommonCodeGroupRepositoryCustom {

  Page<CommonCodeGroup> searchGroups(CommonCodeGroupListRequest condition, Pageable pageable);
}
