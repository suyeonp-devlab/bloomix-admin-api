package com.bloomix.admin.domain.batch.job.kopis.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KopisFacilityDetail(
  String mt10id,        // 공연시설ID
  String fcltynm,       // 공연시설명
  String mt13cnt,       // 공연장 수
  String fcltychartr,   // 시설특성
  String opende,        // 개관연도
  String seatscale,     // 객석 수
  String telno,         // 전화번호
  String relateurl,     // 홈페이지
  String adres,         // 주소
  String la,            // 위도
  String lo,            // 경도
  String restaurant,    // 레스토랑 (Y/N)
  String cafe,          // 카페 (Y/N)
  String store,         // 편의점 (Y/N)
  String nolibang,      // 놀이방 (Y/N)
  String suyu,          // 수유실 (Y/N)
  String parkbarrier,   // 장애시설-주차장 (Y/N)
  String restbarrier,   // 장애시설-화장실 (Y/N)
  String runwbarrier,   // 장애시설-경사로 (Y/N)
  String elevbarrier,   // 장애시설-엘리베이터 (Y/N)
  String parkinglot,    // 주차시설 (Y/N)

  // 공연장 목록 (<mt13s><mt13>…</mt13></mt13s>)
  @JacksonXmlElementWrapper(localName = "mt13s")
  @JacksonXmlProperty(localName = "mt13")
  List<KopisHall> mt13s
) {
}
