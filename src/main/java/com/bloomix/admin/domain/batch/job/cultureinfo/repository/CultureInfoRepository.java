package com.bloomix.admin.domain.batch.job.cultureinfo.repository;

import com.bloomix.admin.domain.batch.job.cultureinfo.entity.CultureInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CultureInfoRepository extends JpaRepository<CultureInfo, String>, CultureInfoRepositoryCustom {
}
