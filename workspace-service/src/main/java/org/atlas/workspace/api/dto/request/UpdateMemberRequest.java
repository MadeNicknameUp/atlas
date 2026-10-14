package org.atlas.workspace.api.dto.request;

import org.atlas.workspace.api.dto.command.UpdateMemberCommand;

public record UpdateMemberRequest(
        // role
        // state?
) {
    public UpdateMemberCommand toCommand() {
        // toCommand()
        return new UpdateMemberCommand(
                // role
                // state
        );
    }
}
