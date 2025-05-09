package me.performancereservation.api;


import lombok.RequiredArgsConstructor;
import me.performancereservation.domain.file.FileService;
import me.performancereservation.domain.file.dto.UploadFileResponse;
import me.performancereservation.domain.performance.dto.performance.request.PerformanceCreateRequest;
import me.performancereservation.domain.performance.dto.performance.request.PerformanceUpdateRequest;
import me.performancereservation.domain.performance.dto.performance.response.PerformanceManagerDetailResponse;
import me.performancereservation.domain.performance.dto.performance.response.PerformanceManagerListResponse;
import me.performancereservation.domain.performance.service.PerformanceScheduleService;
import me.performancereservation.domain.performance.service.PerformanceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/managers")
@RequiredArgsConstructor
public class ManagerPerformanceController {
    private final PerformanceService performanceService;
    private final PerformanceScheduleService performanceScheduleService;
    private final FileService fileService;

    /** 공연자 자신의 공연 목록 페이지 호출
     *
     * 시큐리티 개발후 인증 시스템에서 매니저 Id 추출하는 방향으로 수정 필요
     * @param pageable
     * @param managerId
     * @return 200 + Page<PerformanceManagerListResponse>
     */
    @GetMapping("/performances")
    public ResponseEntity<Page<PerformanceManagerListResponse>> getPerformances(
            @PageableDefault(
                    size=10,
                    sort = "performanceDate",
                    direction = Sort.Direction.DESC) Pageable pageable,
            Long managerId) {
        Page<PerformanceManagerListResponse> performanceManagerResponses = performanceService.getPerformanceManagerList(pageable, managerId);
        return ResponseEntity.ok(performanceManagerResponses);
    }

    /** 공연자 공연 상세 페이지
     *
     * 공연 기본 정보(아이디, 파일경로, 제목, 장소, 상태, 총 좌석수) + 회차 정보(아이디, 시작시간, 종료시간, 남은 좌석, 취소 여부)
     * @param performanceId
     * @param managerId
     * @return 200 + performanceManagerResponse
     */
    @GetMapping("/performances/{performanceId}")
    public ResponseEntity<PerformanceManagerDetailResponse> getPerformanceDetails(@PathVariable Long performanceId, Long managerId) {
        PerformanceManagerDetailResponse performanceManagerResponse = performanceService.getPerformanceManagerDetail(performanceId, managerId);
        return ResponseEntity.ok(performanceManagerResponse);
    }

    /** 공연자 공연 등록 호출
     *
     * 매니저 id 시큐리티 개발 후 수정 필요
     * @param request
     * @param image
     * @param managerId
     * @return 201 + performanceId
     */
    /*@PostMapping("/register")
    public ResponseEntity<Long> registerPerformance(
            @RequestPart("request") PerformanceCreateRequest request,
            @RequestPart("image") MultipartFile image,
            Long managerId
            ) {

        UploadFileResponse uploadFile = fileService.upload(image);
        Long performanceId = performanceService.createPerformance(request, uploadFile.id(), managerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(performanceId);
    }*/

    /** 공연 수정 호출
     *
     * 공연자 아이디 인증 객체로 변경 필요
     * @param performanceId
     * @param request
     * @param managerId
     */
    @PatchMapping("/performance/{performanceId}")
    public ResponseEntity<Void> updatePerformance(@PathVariable Long performanceId,
                                                  @RequestBody PerformanceUpdateRequest request,
                                                  Long managerId) {
        performanceService.updatePerformance(performanceId, request, managerId);
        return ResponseEntity.noContent().build();
    }

    /** 공연 전체 취소 호출
     *
     * 공연 상태 취소 변경 + 연관된 모든 회차 상태 취소 변경
     * 공연자 아이디 인증 객체로 변경 필요
     * @param performanceId
     * @param managerId
     */
    @PatchMapping("/performances/{performanceId}")
    public ResponseEntity<Void> cancelPerformance(@PathVariable Long performanceId, Long managerId) {
        performanceService.cancelPerformance(performanceId, managerId);
        return ResponseEntity.noContent().build();
    }

    /** 회차 취소 호출
     *
     * 선택한 회차에 대한 단일 취소
     * 공연자 아이디 인증 객체로 변경 필요
     * @param performanceId
     * @param performanceScheduleId
     * @param managerId
     */
    @PatchMapping("/performances/{performanceId}/schedules/{performanceScheduleId}")
    public ResponseEntity<Void> cancelPerformanceSchedule(@PathVariable Long performanceId,
                                                          @PathVariable Long performanceScheduleId,
                                                          Long managerId) {
        performanceScheduleService.cancelPerformanceSchedule(performanceId, performanceScheduleId, managerId);
        return ResponseEntity.noContent().build();
    }








}
