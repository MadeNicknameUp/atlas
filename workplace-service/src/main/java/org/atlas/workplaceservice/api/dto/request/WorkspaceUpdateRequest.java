package org.atlas.workplaceservice.api.dto.request;

import org.atlas.workplaceservice.api.dto.command.PatchValue;
import org.atlas.workplaceservice.api.dto.command.WorkspaceUpdateCommand;
import org.openapitools.jackson.nullable.JsonNullable;

public record WorkspaceUpdateRequest(
        JsonNullable<String> name,
        JsonNullable<String> description
) {
    public WorkspaceUpdateRequest {
        name = name != null ? name : JsonNullable.undefined();
        description = description != null ? description : JsonNullable.undefined();
    }

    public WorkspaceUpdateCommand toCommand() {

        return new WorkspaceUpdateCommand(
                toPatchValue(name),
                toPatchValue(description)
        );
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
