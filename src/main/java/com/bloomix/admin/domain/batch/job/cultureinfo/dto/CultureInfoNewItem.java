package com.bloomix.admin.domain.batch.job.cultureinfo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CultureInfoNewItem {

  private String serviceType;              // 서비스구분
  private CultureInfoListItem listItem;    // 목록 항목
  private CultureInfoDetail detail;        // 상세

  public CultureInfoNewItem(String serviceType, CultureInfoListItem listItem, CultureInfoDetail detail) {
    this.serviceType = serviceType;
    this.listItem = listItem;
    this.detail = detail;
  }

  public static CultureInfoNewItem of(String serviceType, CultureInfoListItem listItem) {
    return new CultureInfoNewItem(serviceType, listItem, null);
  }
}
