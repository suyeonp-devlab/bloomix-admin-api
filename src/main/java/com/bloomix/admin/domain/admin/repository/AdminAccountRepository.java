package com.bloomix.admin.domain.admin.repository;

import com.bloomix.admin.domain.admin.entity.AdminAccount;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface AdminAccountRepository extends JpaRepository<AdminAccount, String> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<AdminAccount> findWithLockByAdminId(String adminId);
}
