package com.bloomix.admin.domain.batch.job.eventschedule;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.util.StringUtils;

import static com.bloomix.admin.util.DateUtil.HMM;

/** 공연시간 문구 변환 */
public final class EventScheduleParser {

  // 요일 순서
  private static final String DAYS = "월화수목금토일";

  // 시간 (00:00 ~ 23:59)
  private static final String TIME = "(?:[01]?\\d|2[0-3]):[0-5]\\d";

  // 공연시간 형식: 화요일 ~ 금요일(19:30), 토요일(15:00,19:00), HOL(14:00,18:00)
  private static final Pattern SEGMENT = Pattern.compile(
      "(?:([" + DAYS + "])요일(?:\\s*~\\s*([" + DAYS + "])요일)?|HOL)"
          + "\\((" + TIME + "(?:\\s*,\\s*" + TIME + ")*)\\)");

  private EventScheduleParser() {
  }

  /** 기간 내 공연 일시 목록 */
  public static List<LocalDateTime> parse(String text, LocalDate from, LocalDate to, Set<LocalDate> holidays) {

    if (!isValid(text)) throw new IllegalArgumentException("공연시간 형식 오류: " + text);

    Map<DayOfWeek, Set<LocalTime>> dayTimes = new EnumMap<>(DayOfWeek.class);
    Set<LocalTime> holidayTimes = new TreeSet<>();

    Matcher matcher = SEGMENT.matcher(text);
    while (matcher.find()) {
      List<LocalTime> times = times(matcher.group(3));

      if (matcher.group(1) == null) {
        holidayTimes.addAll(times);
        continue;
      }

      DayOfWeek first = dayOfWeek(matcher.group(1));
      DayOfWeek last = matcher.group(2) != null ? dayOfWeek(matcher.group(2)) : first;
      int days = Math.floorMod(last.getValue() - first.getValue(), DAYS.length());
      for (int i = 0; i <= days; i++) {
        dayTimes.computeIfAbsent(first.plus(i), day -> new TreeSet<>()).addAll(times);
      }
    }

    List<LocalDateTime> result = new ArrayList<>();
    from.datesUntil(to.plusDays(1)).forEach(date -> {
      Set<LocalTime> times = holidays.contains(date) && !holidayTimes.isEmpty()
          ? holidayTimes
          : dayTimes.getOrDefault(date.getDayOfWeek(), Set.of());
      times.forEach(time -> result.add(date.atTime(time)));
    });

    return result;
  }

  /** 공연시간 문구 유효성 검사 */
  private static boolean isValid(String text) {
    return StringUtils.hasText(text)
        && SEGMENT.matcher(text).find()
        && !StringUtils.hasText(SEGMENT.matcher(text).replaceAll("").replaceAll("[\\s,]", ""));
  }

  /** 쉼표로 구분된 시간 목록 */
  private static List<LocalTime> times(String text) {
    return Arrays.stream(text.split("\\s*,\\s*"))
        .map(time -> LocalTime.parse(time, HMM))
        .toList();
  }

  /** 요일 글자 → 요일 */
  private static DayOfWeek dayOfWeek(String day) {
    return DayOfWeek.of(DAYS.indexOf(day) + 1);
  }
}
