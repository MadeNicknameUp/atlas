package org.atlas.workspace.exception.dto;

public record BackendExceptionResponse(
        Integer status,
        String message,
        String path
) implements ExceptionResponse {
}
