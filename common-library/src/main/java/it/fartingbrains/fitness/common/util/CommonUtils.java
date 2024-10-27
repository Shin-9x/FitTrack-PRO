package it.fartingbrains.fitness.common.util;

import it.fartingbrains.fitness.common.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public class CommonUtils {
    public static Mono<ResponseEntity<ErrorResponse>> createErrorResponse(String errorMessage, HttpStatus status) {
        return createErrorResponse(errorMessage, String.valueOf(status.value()), status);
    }

    public static Mono<ResponseEntity<ErrorResponse>> createErrorResponse(
            String errorMessage, String customErrorCode, HttpStatus status
    ) {
        int errorCode = status.value();
        ErrorResponse errorResponse = new ErrorResponse(errorCode, customErrorCode, errorMessage);
        return Mono.just(ResponseEntity.status(status).body(errorResponse));
    }
}
