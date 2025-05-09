package me.performancereservation.domain.performance.service;

import lombok.RequiredArgsConstructor;
import me.performancereservation.domain.file.File;
import me.performancereservation.domain.file.FileRepository;
import me.performancereservation.domain.performance.dto.performance.request.PerformanceCreateRequest;
import me.performancereservation.domain.performance.dto.performance.request.PerformanceUpdateRequest;
import me.performancereservation.domain.performance.dto.performance.response.PerformanceDetailResponse;
import me.performancereservation.domain.performance.dto.performance.response.PerformanceListResponse;
import me.performancereservation.domain.performance.dto.performance.response.PerformanceManagerDetailResponse;
import me.performancereservation.domain.performance.dto.performance.response.PerformanceManagerListResponse;
import me.performancereservation.domain.performance.dto.performanceschedule.PerformanceScheduleResponse;
import me.performancereservation.domain.performance.entities.Performance;
import me.performancereservation.domain.performance.entities.PerformanceSchedule;
import me.performancereservation.domain.performance.mapper.PerformanceMapper;
import me.performancereservation.domain.performance.mapper.PerformanceScheduleMapper;
import me.performancereservation.domain.performance.repository.PerformanceRepository;
import me.performancereservation.domain.performance.repository.PerformanceScheduleRepository;
import me.performancereservation.global.exception.ErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PerformanceService {

    private final PerformanceRepository performanceRepository;
    private final PerformanceScheduleRepository performanceScheduleRepository;
    private final FileRepository fileRepository;

    /** 공연 등록 요청
     *
     * 관리자에게 공연 등록 요청을 위해 "PENDING" 상태로 공연을 저장
     * @param request
     * @return performanceId
     */
    @Transactional
//    public Long createPerformance(PerformanceCreateRequest request, Long fileId, Long managerId) {
    public Long createPerformance(PerformanceCreateRequest request) {

//        공연자 id, 파일 id 추가 필요
//        return performanceRepository.save(PerformanceMapper.toEntity(request, fileId, managerId)).getId();
        return performanceRepository.save(PerformanceMapper.toEntity(request)).getId();
    }


    /** 공연 수정
     *
     * @param performanceId
     * @param request
     * @return performanceId
     */
    @Transactional
    public Long updatePerformance(Long performanceId, PerformanceUpdateRequest request) {
        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(() -> ErrorCode.PERFORMANCE_NOT_FOUND.domainException("id=" + performanceId));
        performance.updateFrom(request);
        return performance.getId();
    }

    /** 공연 전체 취소
     *
     * 공연의 상태를 취소로 변경하고 연결된 회차 모두 취소 상태로 변경
     * @param performanceId
     */
    @Transactional
    public void cancelPerformance(Long performanceId) {
        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(() -> ErrorCode.PERFORMANCE_NOT_FOUND.domainException("id=" + performanceId));

        // 공연 취소
        performance.cancel();
        // 해당 공연 회차 전체 취소
        performanceScheduleRepository.findByPerformanceId(performance.getId())
                .forEach(PerformanceSchedule::cancel);

    }

    /** 고객 공연 목록 조회
     *
     * 공연 목록 페이지에서 판매중 상태를 띄우기 위해
     * 회차 테이블에서 남은 좌석수가 1이상이고 공연의 상태가 CONFIRM 인 공연 추출
     *
     * @param pageable
     * @return Page<PerformanceListResponse>
     */
    @Transactional(readOnly = true)
    public Page<PerformanceListResponse> getPerformanceList(Pageable pageable) {
        // 페이징된 공연 조회
        Page<Performance> performances = performanceRepository.findAvailablePerformances(pageable);

        // 페이징된 공연의 파일 id 추출
        List<Long> fileIds = performances.getContent().stream()
                .map(Performance::getFileId)
                .filter(Objects::nonNull)
                .toList();

        // fileId로 조회한 경로 매핑
        Map<Long, String> fileUrlMap = fileRepository.findAllById(fileIds).stream()
                .collect(Collectors.toMap(File::getId, File::getKey));

        // 응답 페이징 반환
        return performances.map(performance -> (
                PerformanceMapper.toListResponse(performance, fileUrlMap.get(performance.getFileId()), true)));
    }


    /** 고객 공연 상세 페이지 조회
     *
     * @param performanceId
     * @return PerformanceDetailResponse
     */
    @Transactional(readOnly = true)
    public PerformanceDetailResponse getPerformanceDetail(Long performanceId) {
        // 공연 조회
        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(() -> ErrorCode.PERFORMANCE_NOT_FOUND.domainException("id=" + performanceId));

        // 회차 조회
        List<PerformanceSchedule> schedules = performanceScheduleRepository
                .findByPerformanceId(performance.getId());

        // 파일 조회
        File file = fileRepository.findById(performance.getFileId())
                .orElseThrow(() -> ErrorCode.FILE_NOT_FOUND.domainException("id=" + performance.getFileId()));


        return PerformanceMapper.toDetailResponse(performance, file.getKey(), schedules);
    }

    /** 공연자 자신의 공연 목록 조회
     *
     * @param pageable
     * @param managerId
     * @return Page<PerformanceManagerListResponse>
     */
    @Transactional(readOnly = true)
    public Page<PerformanceManagerListResponse> getPerformanceManagerList(Pageable pageable, Long managerId) {
        // 공연자의 모든 공연을 페이징 하여 가져옴
        Page<Performance> performances = performanceRepository.findByManagerId(managerId, pageable);

        // 페이징된 공연의 파일 id 추출
        List<Long> fileIds = performances.getContent().stream()
                .map(Performance::getFileId)
                .filter(Objects::nonNull)
                .toList();

        // fileId로 조회한 경로 매핑
        Map<Long, String> fileUrlMap = fileRepository.findAllById(fileIds).stream()
                .collect(Collectors.toMap(File::getId, File::getKey));

        return performances.map(performance ->
                PerformanceMapper.toManagerListResponse(performance, fileUrlMap.get(performance.getFileId())));
    }

    /** 공연 관리자 공연 상세 페이지 조회
     *
     * @param performanceId
     * @return PerformanceManagerDetailResponse
     */
    @Transactional(readOnly = true)
    public PerformanceManagerDetailResponse getPerformanceManagerDetail(Long performanceId) {
        // 해당 공연 조회
        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(() -> ErrorCode.PERFORMANCE_NOT_FOUND.domainException("id=" + performanceId));

        // 연결된 파일 조회
        File file = fileRepository.findById(performance.getFileId())
                .orElseThrow(() -> ErrorCode.FILE_NOT_FOUND.domainException("id=" + performance.getFileId()));

        // 연결된 회차 조회
        List<PerformanceSchedule> schedules = performanceScheduleRepository.findByPerformanceId(performance.getId());

        // 회차 Response 객체 변환
        List<PerformanceScheduleResponse> scheduleResponses = schedules.stream().map(PerformanceScheduleMapper::toResponse).toList();

        return PerformanceMapper.toManagerDetailResponse(performance, file.getKey(), scheduleResponses);
    }

}
