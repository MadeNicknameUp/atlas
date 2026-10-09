package org.atlas.workspace.api.dto.command;

public record CreateWorkspaceCommand(
        String name,
        String iconUrl,
        String description
) {
}
