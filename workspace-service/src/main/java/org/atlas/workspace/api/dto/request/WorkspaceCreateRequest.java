package org.atlas.workspace.api.dto.request;

import org.atlas.workspace.api.dto.command.CreateWorkspaceCommand;

public record WorkspaceCreateRequest(
        String name,
        String iconUrl,
        String description
) {
    public CreateWorkspaceCommand toCommand() {
        return new CreateWorkspaceCommand(name, iconUrl, description);
    }
}
