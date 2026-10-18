package com.bloomix.admin.domain.batch.job.kopis.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/** KOPIS 공통 응답 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record KopisResponse<T>(
  // 결과 목록
  @JacksonXmlElementWrapper(useWrapping = false)
  @JacksonXmlProperty(localName = "db")
  List<T> db
) {

  public List<T> items() {
    return db == null ? List.of() : db;
  }
}
