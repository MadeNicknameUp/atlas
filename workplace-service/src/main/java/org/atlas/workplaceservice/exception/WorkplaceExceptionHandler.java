package org.atlas.workplaceservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.atlas.workplaceservice.exception.dto.ExceptionResponse;
import org.atlas.workplaceservice.exception.unit.NotFoundException;
import org.atlas.workplaceservice.exception.unit.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class WorkplaceExceptionHandler {

    @ExceptionHandler(value = { NotFoundException.class })
    public ResponseEntity<ExceptionResponse> handleNotFoundException(
            HttpServletRequest request,
            NotFoundException exception
    ) {

        logWarning(request, exception);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ExceptionResponse(
                        HttpStatus.NOT_FOUND.value(),
                        exception.getMessage(),
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(value = { ValidationException.class })
    public ResponseEntity<ExceptionResponse> handleValidationException(
            HttpServletRequest request,
            ValidationException exception
    ) {

        logWarning(request, exception);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        exception.getMessage(),
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(value = {
            IllegalArgumentException.class,
            IllegalStateException.class
    })
    public ResponseEntity<ExceptionResponse> handleIllegalArgumentOrStateException(HttpServletRequest request, RuntimeException exception) {

        logWarning(request, exception);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        exception.getMessage(),
                        request.getRequestURI()
                ));
    }


    @ExceptionHandler(value = { Exception.class })
    public ResponseEntity<ExceptionResponse> handleOtherException(HttpServletRequest request, Exception exception) {

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

    private void logWarning(
            HttpServletRequest request,
            Exception exception
    ) {
        log.warn("{}: {}. Happened on: {}.",
                exception.getClass().getSimpleName(),
                exception.getMessage(),
                request.getRequestURI()
        );
    }

}
