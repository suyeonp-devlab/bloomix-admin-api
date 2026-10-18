package com.bloomix.admin.domain.batch.repository;

import com.bloomix.admin.domain.batch.entity.BatchJob;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BatchJobRepository extends JpaRepository<BatchJob, String>, BatchJobRepositoryCustom {

  List<BatchJob> findByEnabledTrueAndCronExpressionIsNotNull();
}
