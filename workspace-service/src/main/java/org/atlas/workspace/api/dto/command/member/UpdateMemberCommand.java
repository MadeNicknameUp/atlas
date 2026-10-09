package org.atlas.workspace.api.dto.command.member;

import org.atlas.workspace.api.dto.command.PatchValue;

public record UpdateMemberCommand(
        PatchValue<String> role
) {
}
