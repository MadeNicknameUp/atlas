package org.atlas.workspace.api.dto.command;

import org.atlas.workspace.api.dto.request.WorkspaceFilter;

public record FindWorkspacesQuery(
        WorkspaceFilter filter,
        Integer page,
        Integer pageSize
) {
}
