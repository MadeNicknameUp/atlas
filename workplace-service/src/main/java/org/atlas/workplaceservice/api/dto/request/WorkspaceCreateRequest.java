package org.atlas.workplaceservice.api.dto.request;

import org.atlas.workplaceservice.api.dto.command.CreateWorkspaceCommand;

public record WorkspaceCreateRequest(
        String name,
        String iconUrl,
        String description
) {
    public CreateWorkspaceCommand toCommand() {
        return new CreateWorkspaceCommand(name, iconUrl, description);
    }
}
