package org.atlas.workspace.api.dto.command;

import org.atlas.workspace.api.dto.request.SearchFilter;

public record FindEntityQuery<T>(
        SearchFilter<T> filter,
        Integer page,
        Integer pageSize
) {
}
