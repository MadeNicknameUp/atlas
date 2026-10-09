package org.atlas.workspace.api.dto.command.workspace;

public record CreateWorkspaceCommand(
        String name,
        String iconUrl,
        String description
) {
}
