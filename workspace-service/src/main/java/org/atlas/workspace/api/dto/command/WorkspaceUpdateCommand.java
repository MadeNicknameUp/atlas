package org.atlas.workspace.api.dto.command;

public record WorkspaceUpdateCommand(
        PatchValue<String> name,
        PatchValue<String> description
) {
}
