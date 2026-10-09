package org.atlas.workplaceservice.api.dto.command;

import org.atlas.workplaceservice.api.dto.request.WorkspaceFilter;

public record FindWorkspacesQuery(
        WorkspaceFilter filter,
        Integer page,
        Integer pageSize
) {
}
