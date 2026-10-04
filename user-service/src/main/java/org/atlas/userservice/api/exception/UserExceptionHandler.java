package org.atlas.userservice.api.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.atlas.userservice.api.exception.dto.ExceptionResponse;
import org.atlas.userservice.api.exception.unit.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

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
    @ExceptionHandler(value = { IllegalArgumentException.class, MethodArgumentNotValidException.class })
    public ResponseEntity<ExceptionResponse> handleBadRequestException(
            HttpServletRequest request,
            Exception exception
    ) {
        log.warn("{}: {}. Happened on: {}.",
                exception.getClass().getSimpleName(),
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        exception.getMessage(),
                        request.getRequestURI()
                ));
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
                        exception.getMessage(),
                        request.getRequestURI()
                ));
    }



}
