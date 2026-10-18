package com.bloomix.admin.domain.batch.entity;

import com.bloomix.admin.domain.BaseEntity;
import com.bloomix.admin.domain.batch.dto.BatchJobRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BatchJob extends BaseEntity {

  @Id
  private String jobId;

  // 배치명
  private String jobName;

  // 설명
  private String description;

  // 자동 실행 주기
  private String cronExpression;

  // 사용 여부
  private boolean enabled;

  // 배치 등록
  public static BatchJob create(BatchJobRequest request) {
    BatchJob entity = new BatchJob();
    entity.jobId = request.jobId();
    entity.update(request);
    return entity;
  }

  // 배치 수정
  public void update(BatchJobRequest request) {
    this.jobName = request.jobName();
    this.description = request.description();
    this.cronExpression = request.cronExpression();
    this.enabled = request.enabled();
  }
}
