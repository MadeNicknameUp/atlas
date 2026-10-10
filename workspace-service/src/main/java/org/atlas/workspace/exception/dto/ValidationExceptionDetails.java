package org.atlas.workspace.exception.dto;

public record ValidationExceptionDetails(
    String field,
    String message,
    String rejectedValue
) {
}
