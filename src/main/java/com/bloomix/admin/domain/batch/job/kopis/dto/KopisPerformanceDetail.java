package com.bloomix.admin.domain.batch.job.kopis.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KopisPerformanceDetail(
  String mt20id,          // 공연ID
  String mt10id,          // 공연시설ID
  String mt13id,          // 공연장ID
  String prfnm,           // 공연명
  String prfpdfrom,       // 공연시작일
  String prfpdto,         // 공연종료일
  String fcltynm,         // 공연시설명
  String prfcast,         // 출연진
  String prfcrew,         // 제작진
  String prfruntime,      // 런타임
  String prfage,          // 관람연령
  String entrpsnm,        // 기획제작사
  String entrpsnmP,       // 제작사
  String entrpsnmA,       // 기획사
  String entrpsnmH,       // 주최
  String entrpsnmS,       // 주관
  String pcseguidance,    // 티켓가격
  String poster,          // 포스터 이미지 경로
  String sty,             // 줄거리
  String area,            // 지역
  String genrenm,         // 장르명
  String openrun,         // 오픈런 (Y/N)
  String visit,           // 내한 (Y/N)
  String child,           // 아동 (Y/N)
  String daehakro,        // 대학로 (Y/N)
  String festival,        // 축제 (Y/N)
  String musicallicense,  // 뮤지컬 라이선스 (Y/N)
  String musicalcreate,   // 뮤지컬 창작 (Y/N)
  String updatedate,      // 최종수정일
  String prfstate,        // 공연상태
  String dtguidance,      // 공연시간
  String frstregdt,       // 최초등록일

  // 소개이미지 목록 (<styurls><styurl>…</styurl></styurls>)
  @JacksonXmlElementWrapper(localName = "styurls")
  @JacksonXmlProperty(localName = "styurl")
  List<String> styurls
) {
}
