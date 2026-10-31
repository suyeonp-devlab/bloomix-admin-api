package com.bloomix.admin.domain.event.entity;

import com.bloomix.admin.domain.BaseEntity;
import com.bloomix.admin.domain.batch.job.eventsync.dto.EventSyncData;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventSourceChange extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long changeId;

  // 행사ID
  private Long eventId;

  // 원본 시작일
  private LocalDate startDate;

  // 원본 종료일
  private LocalDate endDate;

  // 원본 장소ID
  private Long venueId;

  // 상태
  @Enumerated(EnumType.STRING)
  private EventSourceChangeStatus status;

  // 변경 후보 생성
  public static EventSourceChange create(Long eventId, EventSyncData data) {
    EventSourceChange entity = new EventSourceChange();
    entity.eventId = eventId;
    entity.status = EventSourceChangeStatus.PENDING;
    entity.update(data);
    return entity;
  }

  // 변경 후보 갱신
  public void update(EventSyncData data) {
    this.startDate = data.getStartDate();
    this.endDate = data.getEndDate();
    this.venueId = data.getVenueId();
  }

  // 변경 후보 무시 처리
  public void ignore() {
    this.status = EventSourceChangeStatus.IGNORED;
  }
}
