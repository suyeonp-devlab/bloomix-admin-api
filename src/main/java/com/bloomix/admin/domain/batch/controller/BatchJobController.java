package com.bloomix.admin.domain.batch.controller;

import com.bloomix.admin.domain.batch.dto.BatchJobListRequest;
import com.bloomix.admin.domain.batch.dto.BatchJobRequest;
import com.bloomix.admin.domain.batch.dto.BatchJobResponse;
import com.bloomix.admin.domain.batch.dto.BatchJobRunRequest;
import com.bloomix.admin.domain.batch.service.BatchJobService;
import com.bloomix.admin.response.ApiResponse;
import com.bloomix.admin.response.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/batch-jobs")
@RequiredArgsConstructor
public class BatchJobController {

  private final BatchJobService batchJobService;

  /** 배치 목록 조회 */
  @GetMapping
  public ApiResponse<PageResponse<BatchJobResponse>> searchJobs(
      @ParameterObject BatchJobListRequest condition,
      @ParameterObject @PageableDefault(size = 20) Pageable pageable
  ) {
    return ApiResponse.success(batchJobService.searchJobs(condition, pageable));
  }

  /** 배치 상세 조회 */
  @GetMapping("/{jobId}")
  public ApiResponse<BatchJobResponse> getJob(@PathVariable String jobId) {
    return ApiResponse.success(batchJobService.getJob(jobId));
  }

  /** 배치 등록 */
  @PostMapping
  public ApiResponse<Void> createJob(@Valid @RequestBody BatchJobRequest request) {
    batchJobService.createJob(request);
    return ApiResponse.success(null);
  }

  /** 배치 수정 */
  @PutMapping("/{jobId}")
  public ApiResponse<Void> updateJob(
      @PathVariable String jobId,
      @Valid @RequestBody BatchJobRequest request
  ) {
    batchJobService.updateJob(jobId, request);
    return ApiResponse.success(null);
  }

  /** 배치 즉시 실행 */
  @PostMapping("/{jobId}/executions")
  public ApiResponse<Void> runJob(
      @PathVariable String jobId,
      @RequestBody(required = false) BatchJobRunRequest request
  ) {
    batchJobService.runJob(jobId, request);
    return ApiResponse.success(null);
  }
}
