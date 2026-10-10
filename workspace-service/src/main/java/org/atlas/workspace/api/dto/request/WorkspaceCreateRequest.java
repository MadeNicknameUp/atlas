package org.atlas.workspace.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.atlas.workspace.api.dto.command.CreateWorkspaceCommand;

public record WorkspaceCreateRequest(
        @NotBlank @Size(max = 128, message = "Name may not be empty.") String name,
        @Size(max = 1024, message = "IconUrl is way too long. Max size allowed: 1024 characters.") String iconUrl,
        @Size(max = 1024, message = "IconUrl is way too long. Max size allowed: 1024 characters.") String description
) {
    public CreateWorkspaceCommand toCommand() {
        return new CreateWorkspaceCommand(name, iconUrl, description);
    }
}
