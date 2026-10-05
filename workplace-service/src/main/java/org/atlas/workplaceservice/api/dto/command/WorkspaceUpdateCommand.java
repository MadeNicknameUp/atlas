package org.atlas.workplaceservice.api.dto.command;

public record WorkspaceUpdateCommand(
        PatchValue<String> name,
        PatchValue<String> description
) {
}
