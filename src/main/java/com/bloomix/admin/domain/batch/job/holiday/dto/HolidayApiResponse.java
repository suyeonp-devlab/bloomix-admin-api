package com.bloomix.admin.domain.batch.job.holiday.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Getter;
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/** 한국천문연구원 특일 정보 응답 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class HolidayApiResponse {

  private Header header;  // 응답 헤더
  private Body body;      // 응답 본문

  public String resultCode() {
    return header == null ? null : header.getResultCode();
  }

  public String resultMsg() {
    return header == null ? null : header.getResultMsg();
  }

  public List<HolidayItem> items() {
    return body == null || body.getItems() == null ? List.of() : body.getItems();
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  @Getter
  public static class Header {

    // 응답코드
    @JsonAlias("reseultCode")
    private String resultCode;

    // 응답메시지
    private String resultMsg;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  @Getter
  public static class Body {

    // 결과 목록
    @JacksonXmlElementWrapper(localName = "items")
    @JacksonXmlProperty(localName = "item")
    private List<HolidayItem> items;
  }
}
