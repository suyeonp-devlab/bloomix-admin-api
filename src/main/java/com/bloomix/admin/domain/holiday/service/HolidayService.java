package com.bloomix.admin.domain.holiday.service;

import com.bloomix.admin.domain.holiday.dto.HolidayResponse;
import com.bloomix.admin.domain.holiday.repository.HolidayRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HolidayService {

  private final HolidayRepository holidayRepository;

  /** 연도별 공휴일 목록 조회 */
  public List<HolidayResponse> getHolidays(int year) {

    LocalDate from = LocalDate.of(year, 1, 1);
    LocalDate to = LocalDate.of(year, 12, 31);

    return holidayRepository.findAllByHolidayDateBetweenOrderByHolidayDateAsc(from, to).stream()
        .map(HolidayResponse::from)
        .toList();
  }
}
