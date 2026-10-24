package com.bloomix.admin.domain.batch.job.cultureinfo.step;

import com.bloomix.admin.domain.batch.job.cultureinfo.CultureInfoApiException;
import com.bloomix.admin.domain.batch.job.cultureinfo.CultureInfoInvalidDataException;
import com.bloomix.admin.domain.batch.job.cultureinfo.client.CultureInfoClient;
import com.bloomix.admin.domain.batch.job.cultureinfo.dto.CultureInfoListItem;
import com.bloomix.admin.domain.batch.job.cultureinfo.dto.CultureInfoNewItem;
import com.bloomix.admin.domain.batch.job.cultureinfo.repository.CultureInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.ExecutionContext;
import org.springframework.batch.infrastructure.item.ItemStreamReader;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.bloomix.admin.domain.batch.job.cultureinfo.CultureInfoValueParser.text;

/**
 * 문화정보원 문화정보 Reader
 * - 서비스구분(A·B·C)별 기간 목록을 페이지 단위로 조회
 */
@Slf4j
@RequiredArgsConstructor
public class CultureInfoReader implements ItemStreamReader<CultureInfoNewItem> {

  // 서비스구분: A(공연/전시), B(행사/축제), C(교육/체험)
  private static final List<String> SERVICE_TYPES = List.of("A", "B", "C");

  // 페이지당 조회 건수
  private static final int ROWS = 100;

  // 조회 재시도
  private static final int FETCH_ATTEMPTS = 3;
  private static final long FETCH_RETRY_DELAY_MILLIS = 1000;

  // 진행 위치 저장 키
  private static final String KEY_TYPE = "cultureInfo.reader.type";
  private static final String KEY_PAGE = "cultureInfo.reader.page";
  private static final String KEY_OFFSET = "cultureInfo.reader.offset";
  private static final String KEY_COUNT = "cultureInfo.reader.count";

  private final CultureInfoClient cultureInfoClient;
  private final CultureInfoRepository cultureInfoRepository;
  private final LocalDate from;
  private final LocalDate to;
  private final long limit;

  // 진행 위치
  private int typeIndex = 0;
  private int page = 1;
  private int offset = 0;

  // 상세 조회 건수 (limit 도달 시 종료)
  private long count = 0;

  // 현재 페이지 목록
  private List<CultureInfoListItem> buffer;

  // 이번 실행에서 읽은 일련번호 (중복 제외용)
  private final Set<String> readSeqs = new HashSet<>();

  @Override
  public void open(ExecutionContext executionContext) {
    if (executionContext.containsKey(KEY_TYPE)) {
      typeIndex = executionContext.getInt(KEY_TYPE);
      page = executionContext.getInt(KEY_PAGE);
      offset = executionContext.getInt(KEY_OFFSET);
      count = executionContext.getLong(KEY_COUNT);
      log.info("문화정보 목록 이어서 조회. typeIndex={}, page={}, offset={}, count={}", typeIndex, page, offset, count);
    }
  }

  @Override
  public CultureInfoNewItem read() {

    while (typeIndex < SERVICE_TYPES.size() && count < limit) {

      String serviceType = SERVICE_TYPES.get(typeIndex);
      if (buffer == null) buffer = fetch(serviceType, page);

      // 현재 페이지에서 다음 신규 문화정보
      while (offset < buffer.size()) {
        CultureInfoListItem item = buffer.get(offset++);
        String seq = text(item.getSeq());
        if (seq == null) throw new CultureInfoInvalidDataException("일련번호 없음: serviceTp=" + serviceType + ", item=" + item);
        if (readSeqs.add(seq) && !cultureInfoRepository.existsById(seq)) {
          count++;
          return CultureInfoNewItem.of(serviceType, item);
        }
      }

      // 목록이 ROWS보다 적으면 마지막 페이지 → 다음 서비스구분
      if (buffer.size() < ROWS) {
        typeIndex++;
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
    executionContext.putInt(KEY_TYPE, typeIndex);
    executionContext.putInt(KEY_PAGE, page);
    executionContext.putInt(KEY_OFFSET, offset);
    executionContext.putLong(KEY_COUNT, count);
  }

  /** 목록 조회 */
  private List<CultureInfoListItem> fetch(String serviceType, int page) {

    for (int attempt = 1; ; attempt++) {
      try {
        return cultureInfoClient.getInfos(serviceType, from, to, page, ROWS);
      } catch (CultureInfoApiException e) {
        if (attempt >= FETCH_ATTEMPTS) {
          throw new IllegalStateException("문화정보 목록 조회 실패: serviceTp=" + serviceType + ", page=" + page, e);
        }
        log.warn("문화정보 목록 조회 재시도 {}/{}: {}", attempt, FETCH_ATTEMPTS, e.getMessage());
        sleep();
      }
    }
  }

  /** 목록 조회 재시도 전 대기 */
  private static void sleep() {
    try {
      Thread.sleep(FETCH_RETRY_DELAY_MILLIS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("문화정보원 문화정보 목록 조회 중단", e);
    }
  }
}
