package org.atlas.workspace.exception.dto;

public record ExceptionResponse(
        Integer code,
        String message,
        String path
) {
}
