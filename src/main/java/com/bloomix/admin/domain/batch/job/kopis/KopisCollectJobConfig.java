package com.bloomix.admin.domain.batch.job.kopis;

import static com.bloomix.admin.domain.batch.job.kopis.KopisValueParser.text;
import static com.bloomix.admin.util.BatchUtil.skipLogger;
import static com.bloomix.admin.util.DateUtil.parseYyyyMMdd;

import com.bloomix.admin.domain.batch.job.kopis.client.KopisClient;
import com.bloomix.admin.domain.batch.job.kopis.dto.KopisFacilityDetail;
import com.bloomix.admin.domain.batch.job.kopis.dto.KopisPerformanceDetail;
import com.bloomix.admin.domain.batch.job.kopis.dto.KopisPerformanceListItem;
import com.bloomix.admin.domain.batch.job.kopis.entity.KopisFacility;
import com.bloomix.admin.domain.batch.job.kopis.repository.KopisFacilityRepository;
import com.bloomix.admin.domain.batch.job.kopis.repository.KopisPerformanceRepository;
import com.bloomix.admin.domain.batch.job.kopis.step.KopisPerformanceProcessor;
import com.bloomix.admin.domain.batch.job.kopis.step.KopisPerformanceReader;
import com.bloomix.admin.domain.batch.job.kopis.step.KopisPerformanceWriter;
import java.time.LocalDate;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * KOPIS 공연 수집 배치
 * Step 1 변경 공연 → Step 2 신규 공연 → Step 3 공연시설
 */
@Configuration
public class KopisCollectJobConfig {

  public static final String JOB_NAME = "KOPIS_COLLECT";

  // 커밋 단위
  private static final int CHUNK_SIZE = 20;

  // KOPIS 연동 재시도 횟수
  private static final int RETRY_LIMIT = 3;

  // 건너뛰기 허용 건수 (초과 시 Job 실패)
  private static final int SKIP_LIMIT = 50;

  // 기본 수집 기간
  private static final int DEFAULT_DAYS = 90;

  @Bean
  public Job kopisCollectJob(
      JobRepository jobRepository,
      Step kopisChangedPerformanceStep,
      Step kopisNewPerformanceStep,
      Step kopisFacilityStep
  ) {
    return new JobBuilder(JOB_NAME, jobRepository)
        .start(kopisChangedPerformanceStep)
        .next(kopisNewPerformanceStep)
        .next(kopisFacilityStep)
        .build();
  }

  /** [Step 1] 변경 공연: afterdate 이후 등록/수정된 목록 조회 → 상세 조회 */
  @Bean
  public Step kopisChangedPerformanceStep(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      KopisPerformanceReader kopisChangedPerformanceReader,
      KopisPerformanceProcessor kopisChangedPerformanceProcessor,
      KopisPerformanceWriter kopisPerformanceWriter
  ) {
    return new StepBuilder("kopisChangedPerformanceStep", jobRepository)
      .<KopisPerformanceListItem, KopisPerformanceDetail>chunk(CHUNK_SIZE)
      .transactionManager(transactionManager)
      .reader(kopisChangedPerformanceReader)
      .processor(kopisChangedPerformanceProcessor)
      .writer(kopisPerformanceWriter)
      .faultTolerant()
      .retry(KopisApiException.class)
      .retryLimit(RETRY_LIMIT)
      .skip(KopisApiException.class, KopisInvalidDataException.class)
      .skipLimit(SKIP_LIMIT)
      .skipListener(skipLogger(JOB_NAME))
      .build();
  }

  /** [Step 2] 신규 공연: 기간에 해당하는 공연 목록 조회 → DB에 없는 공연만 상세 조회 */
  @Bean
  public Step kopisNewPerformanceStep(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      KopisPerformanceReader kopisNewPerformanceReader,
      KopisPerformanceProcessor kopisNewPerformanceProcessor,
      KopisPerformanceWriter kopisPerformanceWriter
  ) {
    return new StepBuilder("kopisNewPerformanceStep", jobRepository)
        .<KopisPerformanceListItem, KopisPerformanceDetail>chunk(CHUNK_SIZE)
        .transactionManager(transactionManager)
        .reader(kopisNewPerformanceReader)
        .processor(kopisNewPerformanceProcessor)
        .writer(kopisPerformanceWriter)
        .faultTolerant()
        .retry(KopisApiException.class)
        .retryLimit(RETRY_LIMIT)
        .skip(KopisApiException.class, KopisInvalidDataException.class)
        .skipLimit(SKIP_LIMIT)
        .skipListener(skipLogger(JOB_NAME))
        .build();
  }

  /** [Step 3] 공연시설: DB에 없는 공연시설만 상세 조회 */
  @Bean
  public Step kopisFacilityStep(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      ListItemReader<String> kopisFacilityReader,
      ItemProcessor<String, KopisFacilityDetail> kopisFacilityProcessor,
      ItemWriter<KopisFacilityDetail> kopisFacilityWriter
  ) {
    return new StepBuilder("kopisFacilityStep", jobRepository)
        .<String, KopisFacilityDetail>chunk(CHUNK_SIZE)
        .transactionManager(transactionManager)
        .reader(kopisFacilityReader)
        .processor(kopisFacilityProcessor)
        .writer(kopisFacilityWriter)
        .faultTolerant()
        .retry(KopisApiException.class)
        .retryLimit(RETRY_LIMIT)
        .skip(KopisApiException.class, KopisInvalidDataException.class)
        .skipLimit(SKIP_LIMIT)
        .skipListener(skipLogger(JOB_NAME))
        .build();
  }

  /** 변경 공연 Reader */
  @Bean
  @StepScope
  public KopisPerformanceReader kopisChangedPerformanceReader(
      KopisClient kopisClient,
      @Value("#{jobParameters['from']}") String from,
      @Value("#{jobParameters['to']}") String to,
      @Value("#{jobParameters['afterdate']}") String afterDate
  ) {
    LocalDate parsedFrom = parseYyyyMMdd("from", from, LocalDate.now());
    LocalDate parsedTo = parseYyyyMMdd("to", to, parsedFrom.plusDays(DEFAULT_DAYS));
    LocalDate parsedAfterDate = parseYyyyMMdd("afterdate", afterDate, LocalDate.now().minusDays(1));
    return new KopisPerformanceReader(kopisClient, parsedFrom, parsedTo, parsedAfterDate);
  }

  /** 신규 공연 Reader */
  @Bean
  @StepScope
  public KopisPerformanceReader kopisNewPerformanceReader(
      KopisClient kopisClient,
      @Value("#{jobParameters['from']}") String from,
      @Value("#{jobParameters['to']}") String to
  ) {
    LocalDate parsedFrom = parseYyyyMMdd("from", from, LocalDate.now());
    LocalDate parsedTo = parseYyyyMMdd("to", to, parsedFrom.plusDays(DEFAULT_DAYS));
    return new KopisPerformanceReader(kopisClient, parsedFrom, parsedTo, null);
  }

  /** 공연시설 Reader */
  @Bean
  @StepScope
  public ListItemReader<String> kopisFacilityReader(KopisPerformanceRepository kopisPerformanceRepository) {
    return new ListItemReader<>(kopisPerformanceRepository.findMissingFacilityIds());
  }

  /** 변경 공연 Processor */
  @Bean
  public KopisPerformanceProcessor kopisChangedPerformanceProcessor(
      KopisClient kopisClient,
      KopisPerformanceRepository kopisPerformanceRepository
  ) {
    return new KopisPerformanceProcessor(kopisClient, kopisPerformanceRepository, false);
  }

  /** 신규 공연 Processor */
  @Bean
  public KopisPerformanceProcessor kopisNewPerformanceProcessor(
      KopisClient kopisClient,
      KopisPerformanceRepository kopisPerformanceRepository
  ) {
    return new KopisPerformanceProcessor(kopisClient, kopisPerformanceRepository, true);
  }

  /** 공연시설 Processor */
  @Bean
  public ItemProcessor<String, KopisFacilityDetail> kopisFacilityProcessor(KopisClient kopisClient) {

    return facilityId -> {
      KopisFacilityDetail detail = kopisClient.getFacility(facilityId)
        .orElseThrow(() -> new KopisInvalidDataException("시설상세 없음: " + facilityId));

      if (text(detail.getFcltynm()) == null) {
        throw new KopisInvalidDataException("필수값 누락: " + facilityId + ", fcltynm=" + detail.getFcltynm());
      }

      return detail;
    };
  }

  /** 변경 및 신규 공연 Writer */
  @Bean
  public KopisPerformanceWriter kopisPerformanceWriter(KopisPerformanceRepository kopisPerformanceRepository) {
    return new KopisPerformanceWriter(kopisPerformanceRepository);
  }

  /** 공연시설 Writer */
  @Bean
  public ItemWriter<KopisFacilityDetail> kopisFacilityWriter(KopisFacilityRepository kopisFacilityRepository) {
    return chunk -> kopisFacilityRepository.saveAll(
      chunk.getItems().stream().map(KopisFacility::create).toList());
  }
}
