package com.bloomix.admin.domain.batch.job.cultureinfo.client;

import com.bloomix.admin.domain.batch.job.cultureinfo.CultureInfoApiException;
import com.bloomix.admin.domain.batch.job.cultureinfo.dto.CultureInfoDetail;
import com.bloomix.admin.domain.batch.job.cultureinfo.dto.CultureInfoListItem;
import com.bloomix.admin.domain.batch.job.cultureinfo.dto.CulturePlaceDetail;
import com.bloomix.admin.domain.batch.job.cultureinfo.dto.CultureInfoResponse;
import com.bloomix.admin.util.StringUtil;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.dataformat.xml.XmlMapper;

/** 문화정보원 OPEN API 클라이언트: 문화정보목록, 문화정보상세, 문화예술공간상세 */
@Component
public class CultureInfoClient {

  private static final DateTimeFormatter YYYYMMDD = DateTimeFormatter.BASIC_ISO_DATE;
  private static final int MAX_BODY_LOG_LENGTH = 300;

  // 정상 응답코드
  private static final Set<String> SUCCESS_CODES = Set.of("00", "0000");

  // 한눈에보는문화정보조회 서비스
  private final RestClient infoClient;

  // 문화시설조회 서비스
  private final RestClient placeClient;

  private final XmlMapper xmlMapper = XmlMapper.builder().build();
  private final String serviceKey;

  public CultureInfoClient(
      @Value("${culture-info.base-url}") String baseUrl,
      @Value("${culture-info.place-base-url}") String placeBaseUrl,
      @Value("${culture-info.service-key}") String serviceKey,
      @Value("${culture-info.connect-timeout:5s}") Duration connectTimeout,
      @Value("${culture-info.read-timeout:10s}") Duration readTimeout
  ) {

    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    requestFactory.setConnectTimeout(connectTimeout);
    requestFactory.setReadTimeout(readTimeout);

    this.infoClient = RestClient.builder()
      .baseUrl(baseUrl)
      .requestFactory(requestFactory)
      .build();

    this.placeClient = RestClient.builder()
      .baseUrl(placeBaseUrl)
      .requestFactory(requestFactory)
      .build();

    this.serviceKey = serviceKey;
  }

  /** 문화정보목록 조회 */
  public List<CultureInfoListItem> getInfos(
      String serviceType, LocalDate from, LocalDate to, int page, int rows
  ) {

    // serviceTp: A(공연/전시), B(행사/축제), C(교육/체험)
    Map<String, Object> params = new LinkedHashMap<>();
    params.put("serviceTp", serviceType);
    params.put("from", from.format(YYYYMMDD));
    params.put("to", to.format(YYYYMMDD));
    params.put("PageNo", page);
    params.put("numOfrows", rows);

    return callCultureInfo(infoClient, "/period2", params, new TypeReference<>() {});
  }

  /** 문화정보상세 조회 */
  public Optional<CultureInfoDetail> getInfo(String seq) {
    return callCultureInfo(infoClient, "/detail2", Map.of("seq", seq), new TypeReference<CultureInfoResponse<CultureInfoDetail>>() {})
        .stream().findFirst();
  }

  /** 문화예술공간상세 조회 */
  public Optional<CulturePlaceDetail> getPlace(String placeSeq) {
    return callCultureInfo(placeClient, "/detail", Map.of("seq", placeSeq), new TypeReference<CultureInfoResponse<CulturePlaceDetail>>() {})
        .stream().findFirst();
  }

  /** 문화정보원 호출 + XML 변환 */
  private <T> List<T> callCultureInfo(
      RestClient client,
      String path,
      Map<String, Object> params,
      TypeReference<CultureInfoResponse<T>> type
  ) {

    if (!StringUtils.hasText(serviceKey)) {
      throw new CultureInfoApiException("CULTURE_INFO_API_KEY가 설정되지 않았습니다.");
    }

    String requestInfo = path + " " + params;

    // 문화정보원 호출
    byte[] body;
    try {
      body = client.get()
          .uri(builder -> {
            builder.path(path).queryParam("serviceKey", "{serviceKey}");
            params.forEach(builder::queryParam);
            return builder.build(Map.of("serviceKey", serviceKey));
          })
          .retrieve()
          .body(byte[].class);
    } catch (RestClientException e) {
      throw new CultureInfoApiException("문화정보원 호출 실패: " + requestInfo, e);
    }

    if (body == null || body.length == 0) {
      throw new CultureInfoApiException("문화정보원 빈 응답: " + requestInfo);
    }

    // 정상 응답은 <response>로 시작
    String text = new String(body, StandardCharsets.UTF_8);
    String bodyLog = StringUtil.truncate(text.replaceAll("\\s+", " "), MAX_BODY_LOG_LENGTH);

    if (!text.contains("<response")) {
      throw new CultureInfoApiException("문화정보원 오류 응답: " + requestInfo + ", body=" + bodyLog);
    }

    // XML 변환
    CultureInfoResponse<T> response;
    try {
      response = xmlMapper.readValue(body, type);
    } catch (JacksonException e) {
      throw new CultureInfoApiException("문화정보원 응답 변환 실패: " + requestInfo + ", body=" + bodyLog, e);
    }

    // 응답코드 확인
    String resultCode = response.resultCode();
    if (resultCode == null || !SUCCESS_CODES.contains(resultCode)) {
      throw new CultureInfoApiException("문화정보원 오류 응답: " + requestInfo + ", resultCode=" + resultCode + ", resultMsg=" + response.resultMsg());
    }

    return response.items();
  }
}
