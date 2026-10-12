package com.bloomix.admin.domain.user.repository;

import com.bloomix.admin.domain.user.entity.User;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID>, UserRepositoryCustom {
}
