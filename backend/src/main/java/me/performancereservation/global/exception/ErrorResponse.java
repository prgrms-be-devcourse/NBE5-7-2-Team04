package me.performancereservation.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private String message;

    public static ErrorResponse from(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getMessage());
    }
}