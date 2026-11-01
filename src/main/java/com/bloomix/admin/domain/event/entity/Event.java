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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long eventId;

  // 출처
  @Enumerated(EnumType.STRING)
  private EventSourceType sourceType;

  // 출처ID
  private String sourceId;

  // 카테고리
  @Enumerated(EnumType.STRING)
  private EventCategory category;

  // 장르명
  private String genreName;

  // 제목
  private String title;

  // 시작일
  private LocalDate startDate;

  // 종료일
  private LocalDate endDate;

  // 장소ID
  private Long venueId;

  // 포스터 이미지
  private String posterUrl;

  // 소개 이미지 목록
  @JdbcTypeCode(SqlTypes.JSON)
  private List<String> introImages;

  // 가격 안내
  private String priceText;

  // 소개
  private String description;

  // 출연진
  private String castMembers;

  // 런타임
  private String runtime;

  // 관람연령
  private String ageLimit;

  // 정원
  private int capacity;

  // 판매 여부
  private boolean onSale;

  // 판매 중단 사유
  private String saleStopReason;

  // 판매 중단 일시
  private LocalDateTime saleStoppedAt;

  // 노출 여부
  private boolean enabled;

  // 확인 여부
  private boolean verified;

  // 원본 확인 일시
  private LocalDateTime sourceCheckedAt;

  // 행사 생성
  public static Event create(EventSyncData data) {
    Event entity = new Event();
    entity.sourceType = data.getSourceType();
    entity.sourceId = data.getSourceId();
    entity.category = data.getCategory();
    entity.genreName = data.getGenreName();
    entity.title = data.getTitle();
    entity.startDate = data.getStartDate();
    entity.endDate = data.getEndDate();
    entity.venueId = data.getVenueId();
    entity.posterUrl = data.getPosterUrl();
    entity.introImages = data.getIntroImages();
    entity.priceText = data.getPriceText();
    entity.description = data.getDescription();
    entity.castMembers = data.getCastMembers();
    entity.runtime = data.getRuntime();
    entity.ageLimit = data.getAgeLimit();
    entity.capacity = data.getCapacity();
    entity.onSale = true;
    entity.enabled = true;
    entity.verified = data.getVenueId() != null;
    entity.sourceCheckedAt = LocalDateTime.now();
    return entity;
  }

  // 원본 변경 여부
  public boolean isChangedBy(EventSyncData data) {
    return !this.startDate.equals(data.getStartDate())
        || !this.endDate.equals(data.getEndDate())
        || !Objects.equals(this.venueId, data.getVenueId());
  }

  // 원본 확인 처리
  public void markChecked() {
    this.sourceCheckedAt = LocalDateTime.now();
  }
}
