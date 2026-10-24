package com.bloomix.admin.domain.batch.job.cultureinfo;

import com.bloomix.admin.domain.batch.job.cultureinfo.client.CultureInfoClient;
import com.bloomix.admin.domain.batch.job.cultureinfo.dto.CultureInfoDetail;
import com.bloomix.admin.domain.batch.job.cultureinfo.dto.CultureInfoNewItem;
import com.bloomix.admin.domain.batch.job.cultureinfo.dto.CulturePlaceDetail;
import com.bloomix.admin.domain.batch.job.cultureinfo.entity.CultureInfo;
import com.bloomix.admin.domain.batch.job.cultureinfo.entity.CulturePlace;
import com.bloomix.admin.domain.batch.job.cultureinfo.repository.CultureInfoRepository;
import com.bloomix.admin.domain.batch.job.cultureinfo.repository.CulturePlaceRepository;
import com.bloomix.admin.domain.batch.job.cultureinfo.step.CultureInfoReader;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.bloomix.admin.domain.batch.job.cultureinfo.CultureInfoValueParser.date;
import static com.bloomix.admin.domain.batch.job.cultureinfo.CultureInfoValueParser.text;
import static com.bloomix.admin.util.BatchUtil.skipLogger;
import static com.bloomix.admin.util.DateUtil.parseYyyyMMdd;

/**
 * 문화정보원 문화정보 수집 배치
 * Step 1 신규 문화정보 → Step 2 문화정보 갱신 → Step 3 문화예술공간
 */
@Configuration
public class CultureInfoCollectJobConfig {

  public static final String JOB_NAME = "CULTURE_INFO_COLLECT";

  // 커밋 단위
  private static final int CHUNK_SIZE = 20;

  // 문화정보원 연동 재시도 횟수
  private static final int RETRY_LIMIT = 3;

  // 건너뛰기 허용 건수 (초과 시 Job 실패)
  private static final int SKIP_LIMIT = 50;

  // 기본 수집 기간
  private static final int DEFAULT_DAYS = 90;

  // 기본 상세 조회 한도: 신규 + 갱신
  private static final long DEFAULT_DETAIL_LIMIT = 9500;

  // 기본 문화예술공간 조회 한도
  private static final long DEFAULT_PLACE_LIMIT = 9500;

  // 갱신 상세 조회 최대 건수
  private static final long REFRESH_LIMIT = 100;

  // Step 1 (신규 문화정보)에서 쓴 상세 조회 건수
  private static final String KEY_NEW_COUNT = "cultureInfo.newCount";

  @Bean
  public Job cultureInfoCollectJob(
      JobRepository jobRepository,
      Step cultureInfoNewStep,
      Step cultureInfoRefreshStep,
      Step culturePlaceStep
  ) {
    return new JobBuilder(JOB_NAME, jobRepository)
        .start(cultureInfoNewStep)
        .next(cultureInfoRefreshStep)
        .next(culturePlaceStep)
        .build();
  }

  /** [Step 1] 신규 문화정보: 기간 목록 조회 → DB에 없는 문화정보만 상세 조회 */
  @Bean
  public Step cultureInfoNewStep(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      CultureInfoReader cultureInfoNewReader,
      ItemProcessor<CultureInfoNewItem, CultureInfoNewItem> cultureInfoNewProcessor,
      ItemWriter<CultureInfoNewItem> cultureInfoNewWriter
  ) {
    return new StepBuilder("cultureInfoNewStep", jobRepository)
        .<CultureInfoNewItem, CultureInfoNewItem>chunk(CHUNK_SIZE)
        .transactionManager(transactionManager)
        .reader(cultureInfoNewReader)
        .processor(cultureInfoNewProcessor)
        .writer(cultureInfoNewWriter)
        .faultTolerant()
        .retry(CultureInfoApiException.class)
        .retryLimit(RETRY_LIMIT)
        .skip(CultureInfoApiException.class, CultureInfoInvalidDataException.class)
        .skipLimit(SKIP_LIMIT)
        .skipListener(skipLogger(JOB_NAME))
        .listener(newCountRecorder())
        .build();
  }

  /** [Step 2] 문화정보 갱신: 종료되지 않은 문화정보 중 상세 수집이 오래된 순으로 남은 한도만큼 상세 조회 */
  @Bean
  public Step cultureInfoRefreshStep(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      ListItemReader<String> cultureInfoRefreshReader,
      ItemProcessor<String, CultureInfoDetail> cultureInfoRefreshProcessor,
      ItemWriter<CultureInfoDetail> cultureInfoRefreshWriter
  ) {
    return new StepBuilder("cultureInfoRefreshStep", jobRepository)
        .<String, CultureInfoDetail>chunk(CHUNK_SIZE)
        .transactionManager(transactionManager)
        .reader(cultureInfoRefreshReader)
        .processor(cultureInfoRefreshProcessor)
        .writer(cultureInfoRefreshWriter)
        .faultTolerant()
        .retry(CultureInfoApiException.class)
        .retryLimit(RETRY_LIMIT)
        .skip(CultureInfoApiException.class, CultureInfoInvalidDataException.class)
        .skipLimit(SKIP_LIMIT)
        .skipListener(skipLogger(JOB_NAME))
        .build();
  }

  /** [Step 3] 문화예술공간: DB에 없는 문화예술공간만 상세 조회 */
  @Bean
  public Step culturePlaceStep(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      ListItemReader<String> culturePlaceReader,
      ItemProcessor<String, CulturePlaceDetail> culturePlaceProcessor,
      ItemWriter<CulturePlaceDetail> culturePlaceWriter
  ) {
    return new StepBuilder("culturePlaceStep", jobRepository)
        .<String, CulturePlaceDetail>chunk(CHUNK_SIZE)
        .transactionManager(transactionManager)
        .reader(culturePlaceReader)
        .processor(culturePlaceProcessor)
        .writer(culturePlaceWriter)
        .faultTolerant()
        .retry(CultureInfoApiException.class)
        .retryLimit(RETRY_LIMIT)
        .skip(CultureInfoApiException.class, CultureInfoInvalidDataException.class)
        .skipLimit(SKIP_LIMIT)
        .skipListener(skipLogger(JOB_NAME))
        .build();
  }

  /** 신규 문화정보 Reader */
  @Bean
  @StepScope
  public CultureInfoReader cultureInfoNewReader(
      CultureInfoClient cultureInfoClient,
      CultureInfoRepository cultureInfoRepository,
      @Value("#{jobParameters['from']}") String from,
      @Value("#{jobParameters['to']}") String to
  ) {
    LocalDate parsedFrom = parseYyyyMMdd("from", from, LocalDate.now());
    LocalDate parsedTo = parseYyyyMMdd("to", to, parsedFrom.plusDays(DEFAULT_DAYS));
    return new CultureInfoReader(cultureInfoClient, cultureInfoRepository, parsedFrom, parsedTo, DEFAULT_DETAIL_LIMIT);
  }

  /** 문화정보 갱신 Reader */
  @Bean
  @StepScope
  public ListItemReader<String> cultureInfoRefreshReader(
      CultureInfoRepository cultureInfoRepository,
      @Value("#{jobExecutionContext['" + KEY_NEW_COUNT + "']}") Long newCount
  ) {
    long limit = Math.min(REFRESH_LIMIT, DEFAULT_DETAIL_LIMIT - (newCount == null ? 0 : newCount));
    if (limit <= 0) return new ListItemReader<>(List.of());

    LocalDate today = LocalDate.now();
    return new ListItemReader<>(cultureInfoRepository.findRefreshTargetSeqs(today, today.atStartOfDay(), limit));
  }

  /** 문화예술공간 Reader */
  @Bean
  @StepScope
  public ListItemReader<String> culturePlaceReader(CultureInfoRepository cultureInfoRepository) {
    return new ListItemReader<>(cultureInfoRepository.findMissingPlaceSeqs(DEFAULT_PLACE_LIMIT));
  }

  /** 신규 문화정보 Processor */
  @Bean
  public ItemProcessor<CultureInfoNewItem, CultureInfoNewItem> cultureInfoNewProcessor(CultureInfoClient cultureInfoClient) {
    return item -> new CultureInfoNewItem(
      item.getServiceType(),
      item.getListItem(),
      getValidDetail(cultureInfoClient, text(item.getListItem().getSeq()))
    );
  }

  /** 문화정보 갱신 Processor */
  @Bean
  public ItemProcessor<String, CultureInfoDetail> cultureInfoRefreshProcessor(CultureInfoClient cultureInfoClient) {
    return seq -> getValidDetail(cultureInfoClient, seq);
  }

  /** 문화예술공간 Processor */
  @Bean
  public ItemProcessor<String, CulturePlaceDetail> culturePlaceProcessor(CultureInfoClient cultureInfoClient) {

    return placeSeq -> {
      CulturePlaceDetail detail = cultureInfoClient.getPlace(placeSeq)
          .orElseThrow(() -> new CultureInfoInvalidDataException("문화예술공간상세 없음: " + placeSeq));

      if (text(detail.getCulName()) == null) {
        throw new CultureInfoInvalidDataException("필수값 누락: " + placeSeq + ", culName=" + detail.getCulName());
      }

      return detail;
    };
  }

  /** 신규 문화정보 Writer */
  @Bean
  public ItemWriter<CultureInfoNewItem> cultureInfoNewWriter(CultureInfoRepository cultureInfoRepository) {
    return chunk -> cultureInfoRepository.saveAll(
        chunk.getItems().stream()
            .map(item -> CultureInfo.create(item.getServiceType(), item.getListItem(), item.getDetail()))
            .toList());
  }

  /** 문화정보 갱신 Writer */
  @Bean
  public ItemWriter<CultureInfoDetail> cultureInfoRefreshWriter(CultureInfoRepository cultureInfoRepository) {

    return chunk -> {
      List<String> seqs = chunk.getItems().stream().map(detail -> text(detail.getSeq())).toList();

      Map<String, CultureInfo> existing = cultureInfoRepository.findAllById(seqs).stream()
          .collect(Collectors.toMap(CultureInfo::getSeq, Function.identity()));

      for (CultureInfoDetail detail : chunk) {
        existing.get(text(detail.getSeq())).update(detail);
      }
    };
  }

  /** 문화예술공간 Writer */
  @Bean
  public ItemWriter<CulturePlaceDetail> culturePlaceWriter(CulturePlaceRepository culturePlaceRepository) {
    return chunk -> culturePlaceRepository.saveAll(
        chunk.getItems().stream().map(CulturePlace::create).toList());
  }

  /** 문화정보상세 조회 + 필수값 검사 */
  private static CultureInfoDetail getValidDetail(CultureInfoClient cultureInfoClient, String seq) {

    CultureInfoDetail detail = cultureInfoClient.getInfo(seq)
        .orElseThrow(() -> new CultureInfoInvalidDataException("문화정보상세 없음: " + seq));

    // 제목, 시작일, 마감일
    if (text(detail.getTitle()) == null
        || date(detail.getStartDate()) == null
        || date(detail.getEndDate()) == null) {
      throw new CultureInfoInvalidDataException(
          "필수값 누락: " + seq + ", title=" + detail.getTitle() + ", startDate=" + detail.getStartDate() + ", endDate=" + detail.getEndDate());
    }

    return detail;
  }

  /** Step 1 (신규 문화정보)에서 쓴 상세 조회 건수를 Job 실행 컨텍스트에 저장 */
  private static StepExecutionListener newCountRecorder() {
    return new StepExecutionListener() {
      @Override
      public ExitStatus afterStep(StepExecution stepExecution) {
        stepExecution.getJobExecution().getExecutionContext().putLong(KEY_NEW_COUNT, stepExecution.getReadCount());
        return stepExecution.getExitStatus();
      }
    };
  }
}
