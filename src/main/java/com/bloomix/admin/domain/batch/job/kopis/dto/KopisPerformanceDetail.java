package com.bloomix.admin.domain.batch.job.kopis.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Getter;
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class KopisPerformanceDetail {

  private String mt20id;          // 공연ID
  private String mt10id;          // 공연시설ID
  private String mt13id;          // 공연장ID
  private String prfnm;           // 공연명
  private String prfpdfrom;       // 공연시작일
  private String prfpdto;         // 공연종료일
  private String fcltynm;         // 공연시설명
  private String prfcast;         // 출연진
  private String prfcrew;         // 제작진
  private String prfruntime;      // 런타임
  private String prfage;          // 관람연령
  private String entrpsnm;        // 기획제작사
  private String entrpsnmP;       // 제작사
  private String entrpsnmA;       // 기획사
  private String entrpsnmH;       // 주최
  private String entrpsnmS;       // 주관
  private String pcseguidance;    // 티켓가격
  private String poster;          // 포스터 이미지 경로
  private String sty;             // 줄거리
  private String area;            // 지역
  private String genrenm;         // 장르명
  private String openrun;         // 오픈런 (Y/N)
  private String visit;           // 내한 (Y/N)
  private String child;           // 아동 (Y/N)
  private String daehakro;        // 대학로 (Y/N)
  private String festival;        // 축제 (Y/N)
  private String musicallicense;  // 뮤지컬 라이선스 (Y/N)
  private String musicalcreate;   // 뮤지컬 창작 (Y/N)
  private String updatedate;      // 최종수정일
  private String prfstate;        // 공연상태
  private String dtguidance;      // 공연시간
  private String frstregdt;       // 최초등록일

  // 소개이미지 목록 (<styurls><styurl>…</styurl></styurls>)
  @JacksonXmlElementWrapper(localName = "styurls")
  @JacksonXmlProperty(localName = "styurl")
  private List<String> styurls;
}
