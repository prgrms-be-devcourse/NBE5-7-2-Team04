package me.performancereservation.domain.performance.enums;

public enum PerformanceStatus {
    PENDING,    //승인 요청 전
    CONFIRMED,  //승인됨 (등록)
    REJECTED,   //거부됨

    CANCELLED,  //취소됨
    COMPLETED   //공연 완료됨
}
