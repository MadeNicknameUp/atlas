package org.atlas.workspace.exception.dto;

public interface ExceptionResponse {
    Integer status();
    String message();
    String path();
}
