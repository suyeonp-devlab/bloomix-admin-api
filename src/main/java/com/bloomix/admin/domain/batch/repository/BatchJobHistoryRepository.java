package com.bloomix.admin.domain.batch.repository;

import com.bloomix.admin.domain.batch.entity.BatchStatus;
import com.bloomix.admin.domain.batch.entity.BatchJobHistory;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BatchJobHistoryRepository extends JpaRepository<BatchJobHistory, Long>, BatchJobHistoryRepositoryCustom {

  Optional<BatchJobHistory> findByJobIdAndStatus(String jobId, BatchStatus status);

  List<BatchJobHistory> findByStatus(BatchStatus status);

  Optional<BatchJobHistory> findFirstByJobIdOrderByStartedAtDescHistoryIdDesc(String jobId);
}
