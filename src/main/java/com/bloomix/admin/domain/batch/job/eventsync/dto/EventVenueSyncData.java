package com.bloomix.admin.domain.batch.job.eventsync.dto;

import static com.bloomix.admin.util.StringUtil.unescapeHtml;

import com.bloomix.admin.domain.batch.job.cultureinfo.entity.CultureInfo;
import com.bloomix.admin.domain.batch.job.cultureinfo.entity.CulturePlace;
import com.bloomix.admin.domain.batch.job.eventsync.EventRegionParser;
import com.bloomix.admin.domain.batch.job.kopis.entity.KopisFacility;
import com.bloomix.admin.domain.event.entity.EventSourceType;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventVenueSyncData {

  private EventSourceType sourceType;   // 출처
  private String sourceId;              // 출처ID
  private String name;                  // 장소명
  private String address;               // 주소
  private String sidoCode;              // 시도
  private String sigungu;               // 시군구
  private BigDecimal latitude;          // 위도
  private BigDecimal longitude;         // 경도
  private String phone;                 // 전화번호
  private String homepageUrl;           // 홈페이지
  private Integer seatScale;            // 객석수

  /** KOPIS 공연시설 */
  public static EventVenueSyncData from(KopisFacility facility) {
    EventVenueSyncData data = new EventVenueSyncData();
    data.sourceType = EventSourceType.KOPIS;
    data.sourceId = facility.getFacilityId();
    data.name = facility.getName();
    data.address = facility.getAddress();
    data.setRegion(data.address);
    data.latitude = facility.getLatitude();
    data.longitude = facility.getLongitude();
    data.phone = facility.getPhone();
    data.homepageUrl = facility.getHomepageUrl();
    data.seatScale = facility.getSeatScale();
    return data;
  }

  /** 문화정보원 문화예술공간 */
  public static EventVenueSyncData from(CulturePlace place) {
    EventVenueSyncData data = new EventVenueSyncData();
    data.sourceType = EventSourceType.CULTURE_INFO;
    data.sourceId = place.getPlaceSeq();
    data.name = unescapeHtml(place.getName());
    data.address = unescapeHtml(place.getAddress());
    data.setRegion(data.address);
    data.latitude = place.getLatitude();
    data.longitude = place.getLongitude();
    data.phone = place.getPhone();
    data.homepageUrl = place.getHomepageUrl();
    return data;
  }

  /** 문화정보원 문화정보 (문화예술공간이 없는 행사) */
  public static EventVenueSyncData from(CultureInfo info) {
    EventVenueSyncData data = new EventVenueSyncData();
    data.sourceType = EventSourceType.CULTURE_INFO_UNREGISTERED;
    data.sourceId = info.getSeq();
    data.name = unescapeHtml(info.getPlace());
    data.setRegion(info.getArea() + " " + info.getSigungu());
    data.latitude = info.getLatitude();
    data.longitude = info.getLongitude();
    return data;
  }

  // 시도 + 시군구 세팅
  private void setRegion(String region) {
    this.sidoCode = EventRegionParser.sidoCode(region);
    this.sigungu = sidoCode == null ? null : EventRegionParser.sigungu(region);
  }
}
