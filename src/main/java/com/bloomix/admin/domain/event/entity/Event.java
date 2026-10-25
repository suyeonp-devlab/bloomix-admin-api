package com.bloomix.admin.domain.event.entity;

import com.bloomix.admin.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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

  // 노출 여부
  private boolean enabled;

  // 확인 여부
  private boolean verified;

  // 출처 반영일시
  private LocalDateTime sourceSyncedAt;
}
