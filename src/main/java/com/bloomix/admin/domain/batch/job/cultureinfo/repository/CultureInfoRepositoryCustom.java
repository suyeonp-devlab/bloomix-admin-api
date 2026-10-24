package com.bloomix.admin.domain.batch.job.cultureinfo.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface CultureInfoRepositoryCustom {

  List<String> findRefreshTargetSeqs(LocalDate today, LocalDateTime syncedBefore, long limit);

  List<String> findMissingPlaceSeqs(long limit);
}
