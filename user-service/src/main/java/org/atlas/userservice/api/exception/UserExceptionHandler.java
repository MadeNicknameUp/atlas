package org.atlas.userservice.api.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.atlas.userservice.api.exception.dto.ExceptionResponse;
import org.atlas.userservice.api.exception.unit.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class UserExceptionHandler {

    // 404
    @ExceptionHandler(value = { NotFoundException.class })
    public ResponseEntity<ExceptionResponse> handleNotFoundException(
            HttpServletRequest request,
            NotFoundException exception
    ) {
        log.warn("{}: {}. Happened on: {}.",
                exception.getClass().getSimpleName(),
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ExceptionResponse(
                        HttpStatus.NOT_FOUND.value(),
                        exception.getMessage(),
                        request.getRequestURI()
                ));
    }

    // 400
    @ExceptionHandler(value = { IllegalArgumentException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<ExceptionResponse> handleBadRequestException(
            HttpServletRequest request,
            MethodArgumentNotValidException exception
    ) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining("; "));

        log.warn("Validation failed on {}: {}", request.getRequestURI(), message);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse(HttpStatus.BAD_REQUEST.value(), message, request.getRequestURI()));
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> handleUnreadable(HttpServletRequest request, HttpMessageNotReadableException exception) {
        log.warn("Unreadable body on {}: {}", request.getRequestURI(), exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse(400, "Request body is missing or is not valid JSON", request.getRequestURI()));
    }

    // 500
    @ExceptionHandler(value = { Exception.class })
    public ResponseEntity<ExceptionResponse> handleOtherException(
            HttpServletRequest request,
            Exception exception
    ) {
        log.error("{}: {}. Happened on: {}.",
                exception.getClass().getSimpleName(),
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Unexpected server error",
                        request.getRequestURI()
                ));
    }
}
