package org.atlas.userservice.api.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.atlas.userservice.api.exception.dto.ExceptionResponse;
import org.atlas.userservice.api.exception.unit.NotFoundException;
import org.atlas.userservice.api.exception.unit.UserDeactivatedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class UserExceptionHandler extends ResponseEntityExceptionHandler {

    // 404
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFound(HttpServletRequest request, NotFoundException ex) {
        log.warn("NotFoundException: {}. Happened on: {}.", ex.getMessage(), request.getRequestURI());
        return build(HttpStatus.NOT_FOUND, null, ex.getMessage(), request.getRequestURI());
    }

    // 403
    @ExceptionHandler(UserDeactivatedException.class)
    public ResponseEntity<Object> handleDeactivated(HttpServletRequest request, UserDeactivatedException ex) {
        log.warn("UserDeactivatedException: {}. Happened on: {}.", ex.getMessage(), request.getRequestURI());
        return build(HttpStatus.FORBIDDEN, null, ex.getMessage(), request.getRequestURI());
    }

    // 400
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(HttpServletRequest request, IllegalArgumentException ex) {
        log.warn("IllegalArgumentException: {}. Happened on: {}.", ex.getMessage(), request.getRequestURI());
        return build(HttpStatus.BAD_REQUEST, null, ex.getMessage(), request.getRequestURI());
    }

    // 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleOther(HttpServletRequest request, Exception ex) {
        log.error("Unexpected error on {}", request.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, null, "Unexpected server error", request.getRequestURI());
    }

    // Spring MVC
    // 400
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request
    ) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining("; "));
        String path = path(request);
        log.warn("Validation failed on {}: {}", path, message);
        return build(status, headers, message, path);
    }

    // 400
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request
    ) {
        String path = path(request);
        log.warn("Unreadable body on {}: {}", path, ex.getMessage());
        return build(status, headers, "Request body is missing or is not valid JSON", path);
    }

    // 404
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request
    ) {
        String message = (body instanceof ProblemDetail pd && pd.getDetail() != null)
                ? pd.getDetail()
                : ex.getMessage();
        String path = path(request);
        log.warn("{}: {}. Happened on: {}.", ex.getClass().getSimpleName(), message, path);
        return build(statusCode, headers, message, path);
    }

    // ---- helpers ----

    private static ResponseEntity<Object> build(HttpStatusCode status, HttpHeaders headers, String message, String path) {
        ResponseEntity.BodyBuilder builder = ResponseEntity.status(status);
        if (headers != null) builder.headers(headers);
        return builder.body(new ExceptionResponse(status.value(), message, path));
    }

    private static String path(WebRequest request) {
        return request instanceof ServletWebRequest swr ? swr.getRequest().getRequestURI() : "";
    }
}