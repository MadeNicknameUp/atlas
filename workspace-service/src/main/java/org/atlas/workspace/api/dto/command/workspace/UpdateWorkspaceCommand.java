package org.atlas.workspace.api.dto.command.workspace;

import org.atlas.workspace.api.dto.command.PatchValue;

public record UpdateWorkspaceCommand(
        PatchValue<String> name,
        PatchValue<String> description
) {
}
