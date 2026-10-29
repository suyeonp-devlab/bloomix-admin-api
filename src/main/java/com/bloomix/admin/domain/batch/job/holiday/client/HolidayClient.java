package com.bloomix.admin.domain.batch.job.holiday.client;

import com.bloomix.admin.domain.batch.job.holiday.HolidayApiException;
import com.bloomix.admin.domain.batch.job.holiday.dto.HolidayItem;
import com.bloomix.admin.domain.batch.job.holiday.dto.HolidayResponse;
import com.bloomix.admin.util.StringUtil;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import tools.jackson.core.JacksonException;
import tools.jackson.dataformat.xml.XmlMapper;

/** 한국천문연구원 특일 정보 API 클라이언트: 공휴일 */
@Component
public class HolidayClient {

  private static final int MAX_BODY_LOG_LENGTH = 300;

  // 조회 건수
  private static final int ROWS = 100;

  // 정상 응답코드
  private static final String SUCCESS_CODE = "00";

  private final RestClient restClient;
  private final XmlMapper xmlMapper = XmlMapper.builder().build();
  private final String serviceKey;

  public HolidayClient(
      @Value("${holiday.base-url}") String baseUrl,
      @Value("${holiday.service-key}") String serviceKey,
      @Value("${holiday.connect-timeout:5s}") Duration connectTimeout,
      @Value("${holiday.read-timeout:10s}") Duration readTimeout
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

  /** 공휴일 조회 */
  public List<HolidayItem> getHolidays(int year) {

    if (!StringUtils.hasText(serviceKey)) {
      throw new HolidayApiException("HOLIDAY_API_KEY가 설정되지 않았습니다.");
    }

    String requestInfo = "/getRestDeInfo solYear=" + year;

    // 한국천문연구원 특일 정보 호출
    byte[] body;
    try {
      body = restClient.get()
          .uri(builder -> builder.path("/getRestDeInfo")
              .queryParam("serviceKey", "{serviceKey}")
              .queryParam("solYear", year)
              .queryParam("numOfRows", ROWS)
              .build(Map.of("serviceKey", serviceKey)))
          .retrieve()
          .body(byte[].class);
    } catch (RestClientException e) {
      throw new HolidayApiException("특일 정보 호출 실패: " + requestInfo, e);
    }

    if (body == null || body.length == 0) {
      throw new HolidayApiException("특일 정보 빈 응답: " + requestInfo);
    }

    // 정상 응답은 <response>로 시작
    String text = new String(body, StandardCharsets.UTF_8);
    String bodyLog = StringUtil.truncate(text.replaceAll("\\s+", " "), MAX_BODY_LOG_LENGTH);

    if (!text.contains("<response")) {
      throw new HolidayApiException("특일 정보 오류 응답: " + requestInfo + ", body=" + bodyLog);
    }

    // XML 변환
    HolidayResponse response;
    try {
      response = xmlMapper.readValue(body, HolidayResponse.class);
    } catch (JacksonException e) {
      throw new HolidayApiException("특일 정보 응답 변환 실패: " + requestInfo + ", body=" + bodyLog, e);
    }

    // 응답코드 확인
    if (!SUCCESS_CODE.equals(response.resultCode())) {
      throw new HolidayApiException("특일 정보 오류 응답: " + requestInfo + ", resultCode=" + response.resultCode() + ", resultMsg=" + response.resultMsg());
    }

    return response.items();
  }
}
