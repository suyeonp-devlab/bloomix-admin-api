package com.bloomix.admin.domain.user.repository;

import com.bloomix.admin.domain.user.dto.UserListRequest;
import com.bloomix.admin.domain.user.entity.User;
import com.bloomix.admin.domain.user.entity.UserStatus;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepositoryCustom {

  Page<User> searchUsers(UserListRequest condition, Pageable pageable);

  long updateStatus(UUID userId, UserStatus currentStatus, UserStatus newStatus, String adminId);
}
