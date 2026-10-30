package com.bloomix.admin.domain.holiday.entity;

import com.bloomix.admin.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Holiday extends BaseEntity {

  @Id
  private LocalDate holidayDate;

  // 공휴일명
  private String name;

  // 공휴일 생성
  public static Holiday create(LocalDate holidayDate, String name) {
    Holiday entity = new Holiday();
    entity.holidayDate = holidayDate;
    entity.update(name);
    return entity;
  }

  // 공휴일 갱신
  public void update(String name) {
    this.name = name;
  }
}
