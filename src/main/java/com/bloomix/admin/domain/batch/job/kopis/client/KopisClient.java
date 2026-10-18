package com.bloomix.admin.domain.batch.job.kopis.client;

import com.bloomix.admin.domain.batch.job.kopis.KopisApiException;
import com.bloomix.admin.domain.batch.job.kopis.dto.KopisFacilityDetail;
import com.bloomix.admin.domain.batch.job.kopis.dto.KopisPerformanceDetail;
import com.bloomix.admin.domain.batch.job.kopis.dto.KopisPerformanceListItem;
import com.bloomix.admin.domain.batch.job.kopis.dto.KopisResponse;
import com.bloomix.admin.util.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.dataformat.xml.XmlMapper;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** KOPIS OPEN API 클라이언트: 공연목록, 공연상세, 공연시설상세 */
@Component
public class KopisClient {

  private static final DateTimeFormatter YYYYMMDD = DateTimeFormatter.BASIC_ISO_DATE;
  private static final int MAX_BODY_LOG_LENGTH = 300;

  private final RestClient restClient;
  private final XmlMapper xmlMapper = XmlMapper.builder().build();
  private final String serviceKey;

  public KopisClient(
      @Value("${kopis.base-url}") String baseUrl,
      @Value("${kopis.service-key}") String serviceKey,
      @Value("${kopis.connect-timeout:5s}") Duration connectTimeout,
      @Value("${kopis.read-timeout:10s}") Duration readTimeout
  ) {

    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    requestFactory.setConnectTimeout(connectTimeout);
    requestFactory.setReadTimeout(readTimeout);

    this.restClient = RestClient.builder()
      .baseUrl(baseUrl)
      .requestFactory(requestFactory)
      .build();
    this.serviceKey = serviceKey;
  }

  /** 공연목록 조회 (기간 최대 31일, 페이지당 최대 100건) */
  public List<KopisPerformanceListItem> getPerformances(
      LocalDate from, LocalDate to, int page, int rows, LocalDate afterDate
  ) {

    Map<String, Object> params = new LinkedHashMap<>();
    params.put("stdate", from.format(YYYYMMDD));
    params.put("eddate", to.format(YYYYMMDD));
    params.put("cpage", page);
    params.put("rows", rows);
    if (afterDate != null) params.put("afterdate", afterDate.format(YYYYMMDD));

    return callKopis("/pblprfr", params, new TypeReference<>() {});
  }

  /** 공연상세 조회 */
  public Optional<KopisPerformanceDetail> getPerformance(String performanceId) {
    return callKopis("/pblprfr/" + performanceId, Map.of(), new TypeReference<KopisResponse<KopisPerformanceDetail>>() {})
        .stream().findFirst();
  }

  /** 공연시설상세 조회 */
  public Optional<KopisFacilityDetail> getFacility(String facilityId) {
    return callKopis("/prfplc/" + facilityId, Map.of(), new TypeReference<KopisResponse<KopisFacilityDetail>>() {})
        .stream().findFirst();
  }

  /** KOPIS 호출 + XML 변환 */
  private <T> List<T> callKopis(String path, Map<String, Object> params, TypeReference<KopisResponse<T>> type) {

    if (!StringUtils.hasText(serviceKey)) {
      throw new KopisApiException("KOPIS_API_KEY가 설정되지 않았습니다.");
    }

    String requestInfo = path + " " + params;

    // KOPIS 호출
    byte[] body;
    try {
      body = restClient.get()
          .uri(builder -> {
            builder.path(path).queryParam("service", serviceKey);
            params.forEach(builder::queryParam);
            return builder.build();
          })
          .retrieve()
          .body(byte[].class);
    } catch (RestClientException e) {
      throw new KopisApiException("KOPIS 호출 실패: " + requestInfo, e);
    }

    if (body == null || body.length == 0) {
      throw new KopisApiException("KOPIS 빈 응답: " + requestInfo);
    }

    // 정상 응답은 <dbs>로 시작
    String text = new String(body, StandardCharsets.UTF_8);
    String bodyLog = StringUtil.truncate(text.replaceAll("\\s+", " "), MAX_BODY_LOG_LENGTH);

    if (!text.contains("<dbs")) {
      throw new KopisApiException("KOPIS 오류 응답: " + requestInfo + ", body=" + bodyLog);
    }

    // XML 변환
    try {
      return xmlMapper.readValue(body, type).items();
    } catch (JacksonException e) {
      throw new KopisApiException("KOPIS 응답 변환 실패: " + requestInfo + ", body=" + bodyLog, e);
    }
  }
}
