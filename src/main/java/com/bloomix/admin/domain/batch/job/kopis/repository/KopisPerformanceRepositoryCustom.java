package com.bloomix.admin.domain.batch.job.kopis.repository;

import java.util.List;

public interface KopisPerformanceRepositoryCustom {

  List<String> findMissingFacilityIds();
}
