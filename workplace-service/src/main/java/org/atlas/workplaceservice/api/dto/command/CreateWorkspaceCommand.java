package org.atlas.workplaceservice.api.dto.command;

public record CreateWorkspaceCommand(
        String name,
        String iconUrl,
        String description
) {
}
