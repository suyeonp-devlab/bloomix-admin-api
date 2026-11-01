package com.bloomix.admin.domain.batch.job.eventsync.dto;

import static com.bloomix.admin.domain.batch.job.kopis.KopisValueParser.integer;
import static com.bloomix.admin.util.StringUtil.unescapeHtml;

import com.bloomix.admin.domain.batch.job.cultureinfo.entity.CultureInfo;
import com.bloomix.admin.domain.batch.job.kopis.dto.KopisHall;
import com.bloomix.admin.domain.batch.job.kopis.entity.KopisFacility;
import com.bloomix.admin.domain.batch.job.kopis.entity.KopisPerformance;
import com.bloomix.admin.domain.event.entity.EventCategory;
import com.bloomix.admin.domain.event.entity.EventSourceType;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventSyncData {

  // 객석 정보가 없을 때 랜덤 정원 범위
  private static final int MIN_RANDOM_CAPACITY = 30;
  private static final int MAX_RANDOM_CAPACITY = 330;

  private EventSourceType sourceType;   // 출처
  private String sourceId;              // 출처ID
  private EventCategory category;       // 카테고리
  private String genreName;             // 장르명
  private String title;                 // 제목
  private LocalDate startDate;          // 시작일
  private LocalDate endDate;            // 종료일
  private Long venueId;                 // 장소ID
  private String posterUrl;             // 포스터 이미지
  private List<String> introImages;     // 소개 이미지 목록
  private String priceText;             // 가격 안내
  private String description;           // 소개
  private String castMembers;           // 출연진
  private String runtime;               // 런타임
  private String ageLimit;              // 관람연령
  private int capacity;                 // 정원

  /** KOPIS 공연 + 공연시설 + 장소ID */
  public static EventSyncData of(KopisPerformance performance, KopisFacility facility, Long venueId) {
    EventSyncData data = new EventSyncData();
    data.sourceType = EventSourceType.KOPIS;
    data.sourceId = performance.getPerformanceId();
    data.category = EventCategory.PERFORMANCE;
    data.genreName = performance.getGenreName();
    data.title = performance.getTitle();
    data.startDate = performance.getStartDate();
    data.endDate = performance.getEndDate();
    data.venueId = venueId;
    data.posterUrl = performance.getPosterUrl();
    data.introImages = performance.getIntroImages();
    data.priceText = performance.getPriceText();
    data.description = performance.getStory();
    data.castMembers = performance.getCastMembers();
    data.runtime = performance.getRuntime();
    data.ageLimit = performance.getAgeLimit();
    data.capacity = capacity(performance.getHallId(), facility);
    return data;
  }

  /** 문화정보원 문화정보 + 장소ID */
  public static EventSyncData of(CultureInfo info, Long venueId) {
    EventSyncData data = new EventSyncData();
    data.sourceType = EventSourceType.CULTURE_INFO;
    data.sourceId = info.getSeq();
    data.category = category(info.getServiceType());
    data.genreName = unescapeHtml(info.getRealmName());
    data.title = unescapeHtml(info.getTitle());
    data.startDate = info.getStartDate();
    data.endDate = info.getEndDate();
    data.venueId = venueId;
    data.posterUrl = info.getImageUrl() != null ? info.getImageUrl() : info.getThumbnailUrl();
    data.priceText = unescapeHtml(info.getPriceText());
    data.description = unescapeHtml(info.getContents());
    data.capacity = randomCapacity();
    return data;
  }

  // 정원: 공연장 객석 수 → 시설 객석 수 → 랜덤
  private static int capacity(String hallId, KopisFacility facility) {

    if (facility == null) return randomCapacity();

    if (facility.getHalls() != null) {
      for (KopisHall hall : facility.getHalls()) {
        Integer hallSeat = integer(hall.getSeatscale());
        if (Objects.equals(hall.getMt13id(), hallId) && hallSeat != null && hallSeat > 0) return hallSeat;
      }
    }

    Integer facilitySeat = facility.getSeatScale();
    return facilitySeat != null && facilitySeat > 0 ? facilitySeat : randomCapacity();
  }

  // 랜덤 정원
  private static int randomCapacity() {
    return ThreadLocalRandom.current().nextInt(MIN_RANDOM_CAPACITY, MAX_RANDOM_CAPACITY + 1);
  }

  // 문화정보원 서비스구분 → 카테고리
  private static EventCategory category(String serviceType) {
    return switch (serviceType) {
      case "A" -> EventCategory.EXHIBITION;
      case "B" -> EventCategory.FESTIVAL;
      case "C" -> EventCategory.EXPERIENCE;
      default -> throw new IllegalStateException("반영 대상이 아닌 서비스구분: " + serviceType);
    };
  }
}
