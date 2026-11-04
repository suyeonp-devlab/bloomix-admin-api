package com.bloomix.admin.domain.event.entity;

import com.bloomix.admin.domain.BaseEntity;
import com.bloomix.admin.domain.event.dto.EventScheduleRequest;
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

  // 회차 등록
  public static EventSchedule create(Long eventId, EventScheduleRequest request) {
    EventSchedule entity = new EventSchedule();
    entity.eventId = eventId;
    entity.update(request);
    return entity;
  }

  // 회차 수정
  public void update(EventScheduleRequest request) {
    this.scheduleDate = request.scheduleDate();
    this.startTime = request.startTime();
    this.capacity = request.capacity();
    this.enabled = request.enabled();
  }

  // 회차 사용 중지
  public void disable() {
    this.enabled = false;
  }
}
