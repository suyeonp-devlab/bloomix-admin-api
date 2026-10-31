package com.bloomix.admin.domain.event.entity;

import com.bloomix.admin.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventSchedule extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long scheduleId;

  // 행사ID
  private Long eventId;

  // 회차 날짜
  private LocalDate scheduleDate;

  // 시작 시간
  private LocalTime startTime;

  // 정원
  private int capacity;

  // 사용 여부
  private boolean enabled;

  // 회차 생성
  public static EventSchedule create(Event event, LocalDate scheduleDate, LocalTime startTime) {
    EventSchedule entity = new EventSchedule();
    entity.eventId = event.getEventId();
    entity.scheduleDate = scheduleDate;
    entity.startTime = startTime;
    entity.capacity = event.getCapacity();
    entity.enabled = true;
    return entity;
  }
}
