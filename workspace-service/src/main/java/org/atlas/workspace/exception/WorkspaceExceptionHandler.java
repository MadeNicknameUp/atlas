package org.atlas.workspace.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.atlas.workspace.exception.dto.BackendExceptionResponse;
import org.atlas.workspace.exception.dto.ValidationExceptionDetails;
import org.atlas.workspace.exception.dto.ExceptionResponse;
import org.atlas.workspace.exception.dto.ValidationExceptionResponse;
import org.atlas.workspace.exception.unit.NotFoundException;
import org.atlas.workspace.exception.unit.ValidationException;
import org.springframework.boot.context.properties.bind.BindException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class WorkspaceExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNotFoundException(
            HttpServletRequest request,
            NotFoundException exception
    ) {

        logWarning(request, exception);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new BackendExceptionResponse(
                        HttpStatus.NOT_FOUND.value(),
                        exception.getMessage(),
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ExceptionResponse> handleCustomValidationException(
            HttpServletRequest request,
            ValidationException exception
    ) {

        logWarning(request, exception);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new BackendExceptionResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        exception.getMessage(),
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ExceptionResponse> handleValidationException(
            HttpServletRequest request,
            HandlerMethodValidationException exception
    ) {

        logWarning(request, exception);

        ExceptionResponse response = new BackendExceptionResponse(
                HttpStatus.BAD_REQUEST.value(),
                "ValidationException: Provided argument violates validation constraints.",
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidationException(
            HttpServletRequest request,
            MethodArgumentNotValidException exception
    ) {

        logWarning(request, exception);

        List<ValidationExceptionDetails> details = exception
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> {
                    return new ValidationExceptionDetails(
                            fieldError.getField(),
                            fieldError.getDefaultMessage(),
                            String.valueOf(fieldError.getRejectedValue())
                    );
                })
                .toList();

        ExceptionResponse response = new ValidationExceptionResponse(
                HttpStatus.BAD_REQUEST.value(),
                "ValidationException: Provided argument violates validation constraints.",
                request.getRequestURI(),
                details
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(value = {
            IllegalArgumentException.class,
            IllegalStateException.class
    })
    public ResponseEntity<ExceptionResponse> handleIllegalArgumentOrStateException(HttpServletRequest request, RuntimeException exception) {

        logWarning(request, exception);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new BackendExceptionResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        "Invariant violation: This action may not be performed due to resource's current state or an illegal input.",
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(value = {
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class,
            HttpMediaTypeNotSupportedException.class,
            MissingServletRequestParameterException.class,
            BindException.class,
    })
    public ResponseEntity<ExceptionResponse> handleOtherInvalidInputExceptions(
            HttpServletRequest request,
            RuntimeException exception
    ) {

        logWarning(request, exception);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new BackendExceptionResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        "Invariant violation: This action may not be performed due to resource's current state or an illegal input.",
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleOtherException(HttpServletRequest request, Exception exception) {

        log.error("{}: {}. Happened on: {}.",
                exception.getClass().getSimpleName(),
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new BackendExceptionResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Error occurred. Please contact support or try again later.",
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
