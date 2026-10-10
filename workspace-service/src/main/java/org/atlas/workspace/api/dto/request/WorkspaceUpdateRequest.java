package org.atlas.workspace.api.dto.request;

import jakarta.validation.constraints.Size;
import org.atlas.workspace.api.dto.command.PatchValue;
import org.atlas.workspace.api.dto.command.WorkspaceUpdateCommand;
import org.openapitools.jackson.nullable.JsonNullable;

public record WorkspaceUpdateRequest(
        @Size(max = 128, message = "Name length may not exceed 128 characters.")
        JsonNullable<String> name,

        @Size(max = 1024, message = "Description is way too long. Max size allowed: 1024 characters.")
        JsonNullable<String> iconUrl,

        @Size(max = 1024, message = "IconUrl is way too long. Max size allowed: 1024 characters.")
        JsonNullable<String> description
) {
    public WorkspaceUpdateRequest {
        name = name != null ? name : JsonNullable.undefined();
        iconUrl = iconUrl != null ? iconUrl : JsonNullable.undefined();
        description = description != null ? description : JsonNullable.undefined();
    }

    public WorkspaceUpdateCommand toCommand() {

        return new WorkspaceUpdateCommand(
                toPatchValue(name),
                toPatchValue(iconUrl),
                toPatchValue(description)
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
