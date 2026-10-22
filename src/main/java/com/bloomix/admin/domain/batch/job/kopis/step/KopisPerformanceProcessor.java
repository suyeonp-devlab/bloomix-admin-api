package com.bloomix.admin.domain.batch.job.kopis.step;

import com.bloomix.admin.domain.batch.job.kopis.KopisInvalidDataException;
import com.bloomix.admin.domain.batch.job.kopis.client.KopisClient;
import com.bloomix.admin.domain.batch.job.kopis.dto.KopisPerformanceDetail;
import com.bloomix.admin.domain.batch.job.kopis.dto.KopisPerformanceListItem;
import com.bloomix.admin.domain.batch.job.kopis.repository.KopisPerformanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.infrastructure.item.ItemProcessor;

import java.util.Objects;

import static com.bloomix.admin.domain.batch.job.kopis.KopisValueParser.date;
import static com.bloomix.admin.domain.batch.job.kopis.KopisValueParser.text;

/** KOPIS 공연상세 Processor */
@RequiredArgsConstructor
public class KopisPerformanceProcessor implements ItemProcessor<KopisPerformanceListItem, KopisPerformanceDetail> {

  private final KopisClient kopisClient;
  private final KopisPerformanceRepository kopisPerformanceRepository;
  private final boolean newOnly;

  @Override
  public KopisPerformanceDetail process(KopisPerformanceListItem item) {

    String performanceId = Objects.requireNonNull(text(item.mt20id()));

    // newOnly: 이미 DB에 저장된 공연 제외
    if (newOnly && kopisPerformanceRepository.existsById(performanceId)) {
      return null;
    }

    KopisPerformanceDetail detail = kopisClient.getPerformance(performanceId)
        .orElseThrow(() -> new KopisInvalidDataException("공연상세 없음: " + performanceId));

    validate(performanceId, detail);
    return detail;
  }

  /** 공연상세 필수값 검사 */
  private void validate(String performanceId, KopisPerformanceDetail detail) {

    // 공연명, 시작일, 종료일
    if (text(detail.prfnm()) == null
        || date(detail.prfpdfrom()) == null
        || date(detail.prfpdto()) == null) {
      throw new KopisInvalidDataException(
        "필수값 누락: " + performanceId + ", prfnm=" + detail.prfnm() + ", prfpdfrom=" + detail.prfpdfrom() + ", prfpdto=" + detail.prfpdto());
    }
  }
}
