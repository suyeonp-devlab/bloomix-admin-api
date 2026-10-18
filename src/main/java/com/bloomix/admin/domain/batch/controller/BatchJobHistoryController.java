package com.bloomix.admin.domain.batch.controller;

import com.bloomix.admin.domain.batch.dto.BatchJobHistoryListRequest;
import com.bloomix.admin.domain.batch.dto.BatchJobHistoryResponse;
import com.bloomix.admin.domain.batch.service.BatchJobHistoryService;
import com.bloomix.admin.domain.batch.service.BatchJobService;
import com.bloomix.admin.response.ApiResponse;
import com.bloomix.admin.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/batch-job-histories")
@RequiredArgsConstructor
public class BatchJobHistoryController {

  private final BatchJobHistoryService batchJobHistoryService;
  private final BatchJobService batchJobService;

  /** 배치 실행 이력 목록 조회 */
  @GetMapping
  public ApiResponse<PageResponse<BatchJobHistoryResponse>> searchHistories(
      @ParameterObject BatchJobHistoryListRequest condition,
      @ParameterObject @PageableDefault(size = 20) Pageable pageable
  ) {
    return ApiResponse.success(batchJobHistoryService.searchHistories(condition, pageable));
  }

  /** 배치 재시작 */
  @PostMapping("/{historyId}/restart")
  public ApiResponse<Void> restartJob(@PathVariable Long historyId) {
    batchJobService.restartJob(historyId);
    return ApiResponse.success(null);
  }
}
