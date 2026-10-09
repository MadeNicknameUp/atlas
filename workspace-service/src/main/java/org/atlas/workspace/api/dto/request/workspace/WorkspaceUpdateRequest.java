package org.atlas.workspace.api.dto.request.workspace;

import org.atlas.workspace.api.dto.command.PatchValue;
import org.atlas.workspace.api.dto.command.workspace.UpdateWorkspaceCommand;
import org.openapitools.jackson.nullable.JsonNullable;

public record WorkspaceUpdateRequest(
        JsonNullable<String> name,
        JsonNullable<String> description
) {
    public WorkspaceUpdateRequest {
        name = name != null ? name : JsonNullable.undefined();
        description = description != null ? description : JsonNullable.undefined();
    }

    public UpdateWorkspaceCommand toCommand() {

        return new UpdateWorkspaceCommand(
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
