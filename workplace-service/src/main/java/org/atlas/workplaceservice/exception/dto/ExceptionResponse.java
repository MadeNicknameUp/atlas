package org.atlas.workplaceservice.exception.dto;

public record ExceptionResponse(
        Integer code,
        String message,
        String path
) {
}
