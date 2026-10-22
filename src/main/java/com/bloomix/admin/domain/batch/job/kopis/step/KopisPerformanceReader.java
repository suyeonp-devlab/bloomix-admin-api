package com.bloomix.admin.domain.batch.job.kopis.step;

import static com.bloomix.admin.domain.batch.job.kopis.KopisValueParser.text;

import com.bloomix.admin.domain.batch.job.kopis.KopisApiException;
import com.bloomix.admin.domain.batch.job.kopis.KopisInvalidDataException;
import com.bloomix.admin.domain.batch.job.kopis.client.KopisClient;
import com.bloomix.admin.domain.batch.job.kopis.dto.KopisPerformanceListItem;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.ExecutionContext;
import org.springframework.batch.infrastructure.item.ItemStreamReader;

/**
 * KOPIS 공연목록 Reader
 * - 기간을 31일 구간으로 나눠 구간별 & 페이지별로 조회
 */
@Slf4j
public class KopisPerformanceReader implements ItemStreamReader<KopisPerformanceListItem> {

  // KOPIS 조회 제한 (조회 기간 최대 31일 & 페이지당 최대 100건)
  private static final int MAX_DAYS = 31;
  private static final int ROWS = 100;

  // 조회 재시도
  private static final int FETCH_ATTEMPTS = 3;
  private static final long FETCH_RETRY_DELAY_MILLIS = 1000;

  // 진행 위치 저장 키
  private static final String KEY_WINDOW = "kopis.reader.window";
  private static final String KEY_PAGE = "kopis.reader.page";
  private static final String KEY_OFFSET = "kopis.reader.offset";

  private final KopisClient kopisClient;
  private final List<LocalDate[]> windows;
  private final LocalDate afterDate;

  // 진행 위치
  private int windowIndex = 0;
  private int page = 1;
  private int offset = 0;

  // 현재 페이지 목록
  private List<KopisPerformanceListItem> buffer;

  // 이번 실행에서 읽은 공연ID (중복 제외용)
  private final Set<String> readIds = new HashSet<>();

  public KopisPerformanceReader(KopisClient kopisClient, LocalDate from, LocalDate to, LocalDate afterDate) {
    this.kopisClient = kopisClient;
    this.windows = splitWindows(from, to);
    this.afterDate = afterDate;
  }

  @Override
  public void open(ExecutionContext executionContext) {
    if (executionContext.containsKey(KEY_WINDOW)) {
      windowIndex = executionContext.getInt(KEY_WINDOW);
      page = executionContext.getInt(KEY_PAGE);
      offset = executionContext.getInt(KEY_OFFSET);
      log.info("KOPIS 공연목록 이어서 조회. window={}/{}, page={}, offset={}", windowIndex + 1, windows.size(), page, offset);
    }
  }

  @Override
  public KopisPerformanceListItem read() {

    while (windowIndex < windows.size()) {

      if (buffer == null) buffer = fetch(windows.get(windowIndex), page);

      // 현재 페이지에서 다음 공연
      while (offset < buffer.size()) {
        KopisPerformanceListItem item = buffer.get(offset++);
        String id = text(item.mt20id());
        if (id == null) throw new KopisInvalidDataException("공연ID 없음: " + item);
        if (readIds.add(id)) return item;   // 이미 읽은 공연은 건너뜀
      }

      // 목록이 ROWS보다 적으면 마지막 페이지
      if (buffer.size() < ROWS) {
        windowIndex++;
        page = 1;
      } else {
        page++;
      }

      offset = 0;
      buffer = null;
    }

    return null;
  }

  @Override
  public void update(ExecutionContext executionContext) {
    executionContext.putInt(KEY_WINDOW, windowIndex);
    executionContext.putInt(KEY_PAGE, page);
    executionContext.putInt(KEY_OFFSET, offset);
  }

  /** 목록 조회 */
  private List<KopisPerformanceListItem> fetch(LocalDate[] window, int page) {

    for (int attempt = 1; ; attempt++) {
      try {
        return kopisClient.getPerformances(window[0], window[1], page, ROWS, afterDate);
      } catch (KopisApiException e) {
        if (attempt >= FETCH_ATTEMPTS) {
          throw new IllegalStateException("KOPIS 공연목록 조회 실패: " + window[0] + "~" + window[1] + ", page=" + page, e);
        }
        log.warn("KOPIS 공연목록 조회 재시도 {}/{}: {}", attempt, FETCH_ATTEMPTS, e.getMessage());
        sleep();
      }
    }
  }

  /** 기간을 31일 이하 구간으로 분할 */
  private static List<LocalDate[]> splitWindows(LocalDate from, LocalDate to) {

    List<LocalDate[]> result = new ArrayList<>();
    LocalDate start = from;

    while (!start.isAfter(to)) {
      LocalDate end = start.plusDays(MAX_DAYS - 1);
      if (end.isAfter(to)) end = to;
      result.add(new LocalDate[] {start, end});
      start = end.plusDays(1);
    }

    return result;
  }

  /** 목록 조회 재시도 전 대기 */
  private static void sleep() {
    try {
      Thread.sleep(FETCH_RETRY_DELAY_MILLIS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("KOPIS 공연목록 조회 중단", e);
    }
  }
}
