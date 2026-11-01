package com.bloomix.admin.domain.batch.job.cultureinfo.repository;

import com.bloomix.admin.domain.batch.job.cultureinfo.entity.CulturePlace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CulturePlaceRepository extends JpaRepository<CulturePlace, String> {
}
