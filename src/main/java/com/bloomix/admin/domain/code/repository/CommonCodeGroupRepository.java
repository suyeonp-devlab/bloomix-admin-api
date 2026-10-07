package com.bloomix.admin.domain.code.repository;

import com.bloomix.admin.domain.code.entity.CommonCodeGroup;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface CommonCodeGroupRepository extends JpaRepository<CommonCodeGroup, String>, CommonCodeGroupRepositoryCustom {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<CommonCodeGroup> findWithLockByGroupCode(String groupCode);
}
