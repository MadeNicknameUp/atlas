package org.atlas.workspace.api.dto.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateWorkspaceCommand(
        @NotBlank @Size(max = 128) String name,
        @Size(max = 1024) String iconUrl,
        @Size(max = 1024) String description
) {
}
