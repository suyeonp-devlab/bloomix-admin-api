package com.bloomix.admin.domain.batch.repository;

import com.bloomix.admin.domain.batch.dto.BatchJobHistoryListRequest;
import com.bloomix.admin.domain.batch.entity.BatchJobHistory;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BatchJobHistoryRepositoryCustom {

  Page<BatchJobHistory> searchHistories(BatchJobHistoryListRequest condition, Pageable pageable);

  List<BatchJobHistory> findLatestByJobIds(List<String> jobIds);
}
