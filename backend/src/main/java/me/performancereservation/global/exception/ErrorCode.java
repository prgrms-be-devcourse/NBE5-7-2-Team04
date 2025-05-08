package me.performancereservation.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // 시스템
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");

    // 인증 관련

    // 유저 관련

    // 공연 관련

    // 예약 관련

    // 환불 관련

    // 정산 관련


    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public AppException serviceException() {
        return new AppException(this, ErrorType.SERVICE);
    }

    public AppException serviceException(String detail) {
        return new AppException(this, detail, ErrorType.SERVICE);
    }

    public AppException domainException(String detail) {
        return new AppException(this, detail, ErrorType.DOMAIN);
    }

    public AppException persistenceException(String detail) {
        return new AppException(this, detail, ErrorType.PERSISTENCE);
    }
}