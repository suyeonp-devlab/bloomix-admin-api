package com.bloomix.admin.domain.batch.entity;

import com.bloomix.admin.domain.BaseEntity;
import com.bloomix.admin.util.StringUtil;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BatchJobHistory extends BaseEntity {

  private static final int MAX_MESSAGE_LENGTH = 1000;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long historyId;

  // 배치 ID
  private String jobId;

  // Spring Batch 실행 ID
  private Long jobExecutionId;

  // 실행 구분
  @Enumerated(EnumType.STRING)
  private BatchTriggerType triggerType;

  // 실행 파라미터
  @JdbcTypeCode(SqlTypes.JSON)
  private Map<String, String> parameters;

  // 실행 상태
  @Enumerated(EnumType.STRING)
  private BatchStatus status;

  // 시작 일시
  private LocalDateTime startedAt;

  // 종료 일시
  private LocalDateTime endedAt;

  // 결과 메시지
  private String message;

  // 실행 시작
  public static BatchJobHistory start(String jobId, BatchTriggerType triggerType, Map<String, String> parameters) {
    BatchJobHistory entity = new BatchJobHistory();
    entity.jobId = jobId;
    entity.triggerType = triggerType;
    entity.parameters = parameters;
    entity.status = BatchStatus.RUNNING;
    entity.startedAt = LocalDateTime.now();
    return entity;
  }

  // Spring Batch 실행 ID 연결
  public void linkExecution(Long jobExecutionId) {
    this.jobExecutionId = jobExecutionId;
  }

  // 실행 종료
  public void finish(BatchStatus status, String message) {
    this.status = status;
    this.message = StringUtil.truncate(message, MAX_MESSAGE_LENGTH);
    this.endedAt = LocalDateTime.now();
  }
}
