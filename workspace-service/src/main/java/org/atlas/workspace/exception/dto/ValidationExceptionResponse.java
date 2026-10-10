package org.atlas.workspace.exception.dto;

import java.util.List;

public record ValidationExceptionResponse(
        Integer status,
        String message,
        String path,
        List<ValidationExceptionDetails> details
) implements ExceptionResponse {
}
