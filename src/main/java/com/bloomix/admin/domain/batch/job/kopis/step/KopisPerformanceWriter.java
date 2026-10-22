package com.bloomix.admin.domain.batch.job.kopis.step;

import static com.bloomix.admin.domain.batch.job.kopis.KopisValueParser.text;

import com.bloomix.admin.domain.batch.job.kopis.dto.KopisPerformanceDetail;
import com.bloomix.admin.domain.batch.job.kopis.entity.KopisPerformance;
import com.bloomix.admin.domain.batch.job.kopis.repository.KopisPerformanceRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;

/** KOPIS 공연상세 Writer */
@RequiredArgsConstructor
public class KopisPerformanceWriter implements ItemWriter<KopisPerformanceDetail> {

  private final KopisPerformanceRepository kopisPerformanceRepository;

  @Override
  public void write(Chunk<? extends KopisPerformanceDetail> chunk) {

    List<String> ids = chunk.getItems().stream()
        .map(detail -> text(detail.mt20id())).toList();

    Map<String, KopisPerformance> existing = kopisPerformanceRepository.findAllById(ids).stream()
        .collect(Collectors.toMap(KopisPerformance::getPerformanceId, Function.identity()));

    // 기존은 갱신(변경 감지), 신규는 추가
    List<KopisPerformance> created = new ArrayList<>();
    for (KopisPerformanceDetail detail : chunk) {
      KopisPerformance performance = existing.get(text(detail.mt20id()));
      if (performance != null) performance.update(detail);
      else created.add(KopisPerformance.create(detail));
    }

    kopisPerformanceRepository.saveAll(created);
  }
}
