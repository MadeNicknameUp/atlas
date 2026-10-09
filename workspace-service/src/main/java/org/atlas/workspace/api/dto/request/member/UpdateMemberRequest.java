package org.atlas.workspace.api.dto.request.member;

import org.atlas.workspace.api.dto.command.PatchValue;
import org.atlas.workspace.api.dto.command.member.UpdateMemberCommand;
import org.openapitools.jackson.nullable.JsonNullable;

public record UpdateMemberRequest(
        JsonNullable<String> role
) {
    public UpdateMemberCommand toCommand() {

        return new UpdateMemberCommand(toPatchValue(role));
    }

    private PatchValue<String> toPatchValue(JsonNullable<String> value) {

        if (value.isUndefined()) {
            return new PatchValue.Unchanged<>();
        }

        return value.get().isBlank() ?
                new PatchValue.Clear<>() :
                new PatchValue.Set<>(value.get());

    }
}
