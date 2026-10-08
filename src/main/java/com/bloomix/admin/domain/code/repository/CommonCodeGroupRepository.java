package com.bloomix.admin.domain.code.repository;

import com.bloomix.admin.domain.code.entity.CommonCodeGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommonCodeGroupRepository extends JpaRepository<CommonCodeGroup, String>, CommonCodeGroupRepositoryCustom {
}
