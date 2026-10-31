package com.bloomix.admin.domain.event.entity;

import com.bloomix.admin.domain.BaseEntity;
import jakarta.persistence.Entity;
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
public class EventHistory extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long historyId;

  // 행사ID
  private Long eventId;

  // 제목
  private String title;

  // 시작일
  private LocalDate startDate;

  // 종료일
  private LocalDate endDate;

  // 장소ID
  private Long venueId;

  // 정원
  private int capacity;

  // 판매 여부
  private boolean onSale;

  // 판매 중단 사유
  private String saleStopReason;

  // 노출 여부
  private boolean enabled;

  // 확인 여부
  private boolean verified;

  // 이력 생성
  public static EventHistory create(Event event) {
    EventHistory entity = new EventHistory();
    entity.eventId = event.getEventId();
    entity.title = event.getTitle();
    entity.startDate = event.getStartDate();
    entity.endDate = event.getEndDate();
    entity.venueId = event.getVenueId();
    entity.capacity = event.getCapacity();
    entity.onSale = event.isOnSale();
    entity.saleStopReason = event.getSaleStopReason();
    entity.enabled = event.isEnabled();
    entity.verified = event.isVerified();
    return entity;
  }
}
