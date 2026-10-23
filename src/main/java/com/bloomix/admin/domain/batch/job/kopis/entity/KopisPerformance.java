package com.bloomix.admin.domain.batch.job.kopis.entity;

import static com.bloomix.admin.domain.batch.job.kopis.KopisValueParser.date;
import static com.bloomix.admin.domain.batch.job.kopis.KopisValueParser.dateTime;
import static com.bloomix.admin.domain.batch.job.kopis.KopisValueParser.text;
import static com.bloomix.admin.domain.batch.job.kopis.KopisValueParser.yn;

import com.bloomix.admin.domain.BaseEntity;
import com.bloomix.admin.domain.batch.job.kopis.KopisValueParser;
import com.bloomix.admin.domain.batch.job.kopis.dto.KopisPerformanceDetail;
import jakarta.persistence.Entity;
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
public class KopisPerformance extends BaseEntity {

  @Id
  private String performanceId;

  // 공연명
  private String title;

  // 장르
  private String genreName;

  // 공연상태
  private String state;

  // 공연시작일
  private LocalDate startDate;

  // 공연종료일
  private LocalDate endDate;

  // 지역
  private String area;

  // 공연시설ID
  private String facilityId;

  // 공연시설명
  private String facilityName;

  // 공연장ID
  private String hallId;

  // 포스터 이미지경로
  private String posterUrl;

  // 출연진
  private String castMembers;

  // 제작진
  private String crew;

  // 런타임
  private String runtime;

  // 관람연령
  private String ageLimit;

  // 티켓가격
  private String priceText;

  // 공연시간
  private String scheduleText;

  // 줄거리
  private String story;

  // 기획제작사
  private String producer;

  // 제작사
  private String productionCompany;

  // 기획사
  private String planningCompany;

  // 주최
  private String host;

  // 주관
  private String organizer;

  // 오픈런
  private boolean openRun;

  // 내한
  private boolean visit;

  // 아동
  private boolean child;

  // 대학로
  private boolean daehakro;

  // 축제
  private boolean festival;

  // 뮤지컬 라이선스
  private boolean musicalLicense;

  // 뮤지컬 창작
  private boolean musicalCreate;

  // 소개 이미지목록
  @JdbcTypeCode(SqlTypes.JSON)
  private List<String> introImages;

  // 최초등록일
  private LocalDateTime registeredAt;

  // 최종수정일
  private LocalDateTime sourceUpdatedAt;

  // 공연 생성
  public static KopisPerformance create(KopisPerformanceDetail detail) {
    KopisPerformance entity = new KopisPerformance();
    entity.performanceId = text(detail.getMt20id());
    entity.update(detail);
    return entity;
  }

  // 공연 갱신
  public void update(KopisPerformanceDetail detail) {
    this.title = text(detail.getPrfnm());
    this.genreName = text(detail.getGenrenm());
    this.state = text(detail.getPrfstate());
    this.startDate = date(detail.getPrfpdfrom());
    this.endDate = date(detail.getPrfpdto());
    this.area = text(detail.getArea());
    this.facilityId = text(detail.getMt10id());
    this.facilityName = text(detail.getFcltynm());
    this.hallId = text(detail.getMt13id());
    this.posterUrl = text(detail.getPoster());
    this.castMembers = text(detail.getPrfcast());
    this.crew = text(detail.getPrfcrew());
    this.runtime = text(detail.getPrfruntime());
    this.ageLimit = text(detail.getPrfage());
    this.priceText = text(detail.getPcseguidance());
    this.scheduleText = text(detail.getDtguidance());
    this.story = text(detail.getSty());
    this.producer = text(detail.getEntrpsnm());
    this.productionCompany = text(detail.getEntrpsnmP());
    this.planningCompany = text(detail.getEntrpsnmA());
    this.host = text(detail.getEntrpsnmH());
    this.organizer = text(detail.getEntrpsnmS());
    this.openRun = yn(detail.getOpenrun());
    this.visit = yn(detail.getVisit());
    this.child = yn(detail.getChild());
    this.daehakro = yn(detail.getDaehakro());
    this.festival = yn(detail.getFestival());
    this.musicalLicense = yn(detail.getMusicallicense());
    this.musicalCreate = yn(detail.getMusicalcreate());
    this.introImages = detail.getStyurls() == null ? null
      : detail.getStyurls().stream().map(KopisValueParser::text).filter(Objects::nonNull).toList();
    this.registeredAt = dateTime(detail.getFrstregdt());
    this.sourceUpdatedAt = dateTime(detail.getUpdatedate());
  }
}
