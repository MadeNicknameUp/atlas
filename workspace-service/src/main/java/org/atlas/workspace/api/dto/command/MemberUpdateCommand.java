package org.atlas.workspace.api.dto.command;

public record MemberUpdateCommand(
        PatchValue<String> role
) {
}
