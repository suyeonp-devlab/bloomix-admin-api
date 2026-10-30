package com.bloomix.admin.domain.holiday.dto;

import com.bloomix.admin.domain.holiday.entity.Holiday;
import java.time.LocalDate;

public record HolidayResponse(
  LocalDate holidayDate,
  String name
) {

  public static HolidayResponse from(Holiday holiday) {
    return new HolidayResponse(
        holiday.getHolidayDate(),
        holiday.getName()
    );
  }
}
