package com.bloomix.admin.domain.batch.job.eventsync;

import static java.util.Map.entry;

import java.util.Map;
import org.springframework.util.StringUtils;

/** 연동데이터 (KOPIS & 문화정보원) 주소 변환 */
public final class EventRegionParser {

  // 시도 코드 매핑
  private static final Map<String, String> SIDO_CODES = Map.ofEntries(
      entry("서울특별시", "SEOUL"), entry("서울시", "SEOUL"), entry("서울", "SEOUL"),
      entry("부산광역시", "BUSAN"), entry("부산", "BUSAN"),
      entry("대구광역시", "DAEGU"), entry("대구", "DAEGU"),
      entry("인천광역시", "INCHEON"), entry("인천", "INCHEON"),
      entry("대전광역시", "DAEJEON"), entry("대전", "DAEJEON"),
      entry("울산광역시", "ULSAN"), entry("울산", "ULSAN"),
      entry("세종특별자치시", "SEJONG"), entry("세종", "SEJONG"),
      entry("경기도", "GYEONGGI"), entry("경기", "GYEONGGI"),
      entry("강원특별자치도", "GANGWON"), entry("강원도", "GANGWON"), entry("강원", "GANGWON"),
      entry("충청북도", "CHUNGBUK"), entry("충북", "CHUNGBUK"),
      entry("충청남도", "CHUNGNAM"), entry("충남", "CHUNGNAM"),
      entry("전북특별자치도", "JEONBUK"), entry("전라북도", "JEONBUK"), entry("전북", "JEONBUK"),
      entry("전남광주통합특별시", "JEONNAM_GWANGJU"), entry("전남광주통합", "JEONNAM_GWANGJU"),
      entry("광주광역시", "JEONNAM_GWANGJU"), entry("광주", "JEONNAM_GWANGJU"),
      entry("전라남도", "JEONNAM_GWANGJU"), entry("전남", "JEONNAM_GWANGJU"),
      entry("경상북도", "GYEONGBUK"), entry("경북", "GYEONGBUK"),
      entry("경상남도", "GYEONGNAM"), entry("경남", "GYEONGNAM"),
      entry("제주특별자치도", "JEJU"), entry("제주도", "JEJU"), entry("제주", "JEJU")
  );

  private EventRegionParser() {
  }

  /** 시도 코드 */
  public static String sidoCode(String address) {
    String[] words = words(address);
    return words.length == 0 ? null : SIDO_CODES.get(words[0]);
  }

  /** 시군구 */
  public static String sigungu(String address) {
    String[] words = words(address);
    if (words.length < 2) return null;

    String word = words[1];
    return word.endsWith("시") || word.endsWith("군") || word.endsWith("구") ? word : null;
  }

  /** 주소 단어 목록 */
  private static String[] words(String address) {
    return StringUtils.hasText(address) ? address.trim().split("\\s+") : new String[0];
  }
}
