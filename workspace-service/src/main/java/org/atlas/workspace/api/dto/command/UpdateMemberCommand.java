package org.atlas.workspace.api.dto.command;

public record UpdateMemberCommand(
        PatchValue<String> role
) {
}
