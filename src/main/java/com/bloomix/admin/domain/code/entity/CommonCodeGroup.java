package com.bloomix.admin.domain.code.entity;

import com.bloomix.admin.domain.BaseEntity;
import com.bloomix.admin.domain.code.dto.CommonCodeGroupRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommonCodeGroup extends BaseEntity {

  @Id
  private String groupCode;

  // 그룹명
  private String groupName;

  // 설명
  private String description;

  // 사용 여부
  private boolean enabled;

  // 공통코드 그룹 생성
  public static CommonCodeGroup create(CommonCodeGroupRequest request) {
    CommonCodeGroup entity = new CommonCodeGroup();
    entity.groupCode = request.groupCode();
    entity.update(request);
    return entity;
  }

  // 공통코드 그룹 수정
  public void update(CommonCodeGroupRequest request) {
    this.groupName = request.groupName();
    this.description = request.description();
    this.enabled = request.enabled();
  }
}
