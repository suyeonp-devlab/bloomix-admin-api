package com.bloomix.admin.domain.batch.job.holiday.repository;

import com.bloomix.admin.domain.batch.job.holiday.entity.Holiday;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HolidayRepository extends JpaRepository<Holiday, LocalDate> {

  List<Holiday> findAllByHolidayDateBetween(LocalDate from, LocalDate to);
}
