package com.bloomix.admin.domain.batch.repository;

import com.bloomix.admin.domain.batch.dto.BatchJobListRequest;
import com.bloomix.admin.domain.batch.entity.BatchJob;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BatchJobRepositoryCustom {

  Page<BatchJob> searchJobs(BatchJobListRequest condition, Pageable pageable);
}
