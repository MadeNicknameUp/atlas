package org.atlas.userservice.api.exception.dto;

public record ExceptionResponse(
        Integer code,
        String message,
        String path
) {
}
