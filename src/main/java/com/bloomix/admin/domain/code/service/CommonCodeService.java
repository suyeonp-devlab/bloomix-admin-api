package com.bloomix.admin.domain.code.service;

import com.bloomix.admin.domain.code.dto.*;
import com.bloomix.admin.domain.code.entity.CommonCode;
import com.bloomix.admin.domain.code.entity.CommonCodeGroup;
import com.bloomix.admin.domain.code.repository.CommonCodeGroupRepository;
import com.bloomix.admin.domain.code.repository.CommonCodeRepository;
import com.bloomix.admin.exception.BizException;
import com.bloomix.admin.exception.ErrorCode;
import com.bloomix.admin.response.PageResponse;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommonCodeService {

  private final CommonCodeGroupRepository commonCodeGroupRepository;
  private final CommonCodeRepository commonCodeRepository;

  /** 코드 그룹 목록 조회 */
  public PageResponse<CommonCodeGroupListResponse> searchGroups(CommonCodeGroupListRequest condition, Pageable pageable) {
    return PageResponse.from(
      commonCodeGroupRepository.searchGroups(condition, pageable).map(CommonCodeGroupListResponse::from));
  }

  /** 코드 그룹 상세 조회 */
  public CommonCodeGroupResponse getGroup(String groupCode) {

    CommonCodeGroup group = commonCodeGroupRepository.findById(groupCode)
        .orElseThrow(() -> new BizException(ErrorCode.CODE_GROUP_NOT_FOUND));

    List<CommonCode> codes = commonCodeRepository.findByGroupCodeOrderBySortOrderAscCodeAsc(groupCode);
    return CommonCodeGroupResponse.of(group, codes);
  }

  /** 코드 그룹 + 코드 일괄 등록 */
  @Transactional
  public void createGroup(CommonCodeGroupRequest request) {

    validateDuplicateCodes(request.codes());

    if (commonCodeGroupRepository.existsById(request.groupCode())) {
      throw new BizException(ErrorCode.DUPLICATE_CODE_GROUP);
    }

    // 코드 그룹 저장
    commonCodeGroupRepository.save(CommonCodeGroup.create(request));

    // 코드 저장
    List<CommonCode> codes = request.codes().stream()
        .map(code -> CommonCode.create(request.groupCode(), code)).toList();
    commonCodeRepository.saveAll(codes);
  }

  /** 그룹 + 코드 일괄 수정 */
  @Transactional
  public void updateGroup(String groupCode, CommonCodeGroupRequest request) {

    validateDuplicateCodes(request.codes());

    CommonCodeGroup group = commonCodeGroupRepository.findById(groupCode)
        .orElseThrow(() -> new BizException(ErrorCode.CODE_GROUP_NOT_FOUND));

    // 코드 일괄 수정 시 기존 등록 코드를 반드시 포함해야 한다. 미포함 시 400 오류
    Map<String, CommonCode> existingCodes = commonCodeRepository.findByGroupCodeOrderBySortOrderAscCodeAsc(groupCode)
        .stream()
        .collect(Collectors.toMap(CommonCode::getCode, Function.identity()));

    Set<String> requestCodes = request.codes().stream()
        .map(CommonCodeRequest::code)
        .collect(Collectors.toSet());

    List<String> missingCodes = existingCodes.keySet().stream()
        .filter(code -> !requestCodes.contains(code))
        .sorted().toList();

    if (!missingCodes.isEmpty()) {
      throw new BizException(ErrorCode.INVALID_INPUT, "기존 코드가 누락되었습니다");
    }

    // 코드 그룹 수정
    group.update(request);

    // 코드 수정 (기존 코드는 수정, 신규 코드는 등록)
    List<CommonCode> newCodes = new ArrayList<>();

    for (CommonCodeRequest code : request.codes()) {
      CommonCode existing = existingCodes.get(code.code());
      if (existing == null) newCodes.add(CommonCode.create(groupCode, code));
      else existing.update(code);
    }

    commonCodeRepository.saveAll(newCodes);
  }

  /** 셀렉트박스용 코드 조회 */
  public List<CommonCodeResponse> getOptions(String groupCode) {

    CommonCodeGroup group = commonCodeGroupRepository.findById(groupCode)
        .orElseThrow(() -> new BizException(ErrorCode.CODE_GROUP_NOT_FOUND));

    if (!group.isEnabled()) {
      return List.of();
    }

    return commonCodeRepository.findByGroupCodeAndEnabledTrueOrderBySortOrderAscCodeAsc(groupCode).stream()
        .map(CommonCodeResponse::from).toList();
  }

  /** 코드 중복 여부 검사 */
  private void validateDuplicateCodes(List<CommonCodeRequest> codes) {
    Set<String> seen = new HashSet<>();
    for (CommonCodeRequest code : codes) {
      if (!seen.add(code.code())) {
        throw new BizException(ErrorCode.INVALID_INPUT, "중복된 코드가 있습니다: " + code.code());
      }
    }
  }
}
