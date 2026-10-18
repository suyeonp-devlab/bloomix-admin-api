package com.bloomix.admin.domain.batch.job.kopis.repository;

import com.bloomix.admin.domain.batch.job.kopis.entity.KopisPerformance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KopisPerformanceRepository extends JpaRepository<KopisPerformance, String> {
}
