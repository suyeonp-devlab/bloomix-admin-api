package com.bloomix.admin.domain.batch.job.kopis.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Getter;
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class KopisFacilityDetail {

  private String mt10id;        // 공연시설ID
  private String fcltynm;       // 공연시설명
  private String mt13cnt;       // 공연장 수
  private String fcltychartr;   // 시설특성
  private String opende;        // 개관연도
  private String seatscale;     // 객석 수
  private String telno;         // 전화번호
  private String relateurl;     // 홈페이지
  private String adres;         // 주소
  private String la;            // 위도
  private String lo;            // 경도
  private String restaurant;    // 레스토랑 (Y/N)
  private String cafe;          // 카페 (Y/N)
  private String store;         // 편의점 (Y/N)
  private String nolibang;      // 놀이방 (Y/N)
  private String suyu;          // 수유실 (Y/N)
  private String parkbarrier;   // 장애시설-주차장 (Y/N)
  private String restbarrier;   // 장애시설-화장실 (Y/N)
  private String runwbarrier;   // 장애시설-경사로 (Y/N)
  private String elevbarrier;   // 장애시설-엘리베이터 (Y/N)
  private String parkinglot;    // 주차시설 (Y/N)

  // 공연장 목록 (<mt13s><mt13>…</mt13></mt13s>)
  @JacksonXmlElementWrapper(localName = "mt13s")
  @JacksonXmlProperty(localName = "mt13")
  private List<KopisHall> mt13s;
}
