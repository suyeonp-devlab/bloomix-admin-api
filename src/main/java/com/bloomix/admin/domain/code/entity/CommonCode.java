package com.bloomix.admin.domain.code.entity;

import com.bloomix.admin.domain.BaseEntity;
import com.bloomix.admin.domain.code.dto.CommonCodeRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@IdClass(CommonCodeId.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommonCode extends BaseEntity {

  @Id
  private String groupCode;

  @Id
  private String code;

  // 코드명
  private String codeName;

  // 정렬 순서
  private int sortOrder;

  // 사용 여부
  private boolean enabled;

  // 추가 속성
  private String etc1;
  private String etc2;
  private String etc3;

  // 공통코드 생성
  public static CommonCode create(String groupCode, CommonCodeRequest request) {
    CommonCode entity = new CommonCode();
    entity.groupCode = groupCode;
    entity.code = request.code();
    entity.update(request);
    return entity;
  }

  // 공통코드 수정
  public void update(CommonCodeRequest request) {
    this.codeName = request.codeName();
    this.sortOrder = request.sortOrder();
    this.enabled = request.enabled();
    this.etc1 = request.etc1();
    this.etc2 = request.etc2();
    this.etc3 = request.etc3();
  }
}
