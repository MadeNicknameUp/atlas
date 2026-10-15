package org.atlas.workspace.api.dto.request;

import jakarta.validation.constraints.Size;
import org.atlas.workspace.api.dto.command.MemberUpdateCommand;
import org.atlas.workspace.api.dto.command.PatchValue;
import org.openapitools.jackson.nullable.JsonNullable;

public record MemberUpdateRequest(
        @Size(max = 20, message = "Role length may not exceed 20 characters.")
        JsonNullable<String> role
) {

    public MemberUpdateRequest {
        role = role != null ? role : JsonNullable.undefined();
    }

    public MemberUpdateCommand toCommand() {

        return new MemberUpdateCommand(
                toPatchValue(role)
        );
    }

    private PatchValue<String> toPatchValue(JsonNullable<String> value) {

        if (value.isUndefined()) {
            return new PatchValue.Unchanged<>();
        }

        return value.get() == null || value.get().isBlank() ?
                new PatchValue.Clear<>() :
                new PatchValue.Set<>(value.get());

    }

}