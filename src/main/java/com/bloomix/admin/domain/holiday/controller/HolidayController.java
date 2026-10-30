package com.bloomix.admin.domain.holiday.controller;

import com.bloomix.admin.domain.holiday.dto.HolidayResponse;
import com.bloomix.admin.domain.holiday.service.HolidayService;
import com.bloomix.admin.response.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/holidays")
@RequiredArgsConstructor
public class HolidayController {

  private final HolidayService holidayService;

  /** 연도별 공휴일 목록 조회 */
  @GetMapping
  public ApiResponse<List<HolidayResponse>> getHolidays(@RequestParam int year) {
    return ApiResponse.success(holidayService.getHolidays(year));
  }
}
