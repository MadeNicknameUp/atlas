package org.atlas.workspace.api.dto.command;

public record FindDomainQuery<T> (
    T filter,
    Integer page,
    Integer pageSize
) {
}
