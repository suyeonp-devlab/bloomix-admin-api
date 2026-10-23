package com.bloomix.admin.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.listener.SkipListener;

@Slf4j
public final class BatchUtil {

  private BatchUtil() {
  }

  /** 배치 Skip 발생 시 건별 로그 */
  public static <I, O> SkipListener<I, O> skipLogger(String jobName) {

    return new SkipListener<>() {
      @Override
      public void onSkipInRead(Throwable t) {
        log.warn("[{}] 읽기 건너뜀: {}", jobName, t.getMessage());
      }

      @Override
      public void onSkipInProcess(I item, Throwable t) {
        log.warn("[{}] 처리 건너뜀: {}", jobName, t.getMessage());
      }

      @Override
      public void onSkipInWrite(O item, Throwable t) {
        log.warn("[{}] 저장 건너뜀: {}", jobName, t.getMessage());
      }
    };
  }
}
