package org.atlas.workplaceservice.api.dto.request;


import org.atlas.workplaceservice.store.model.Workspace;

import java.time.Instant;
import java.util.Locale;

public record WorkspaceFilter(
        String name,
        String description,
        String state,
        String ownerId,
        Instant from,
        Instant until
) {

    public boolean applyOn(Workspace workspace) {
        if (name != null && !name.isBlank() && (workspace.getName() == null
                || !workspace.getName().toLowerCase(Locale.ROOT).contains(name.toLowerCase(Locale.ROOT)))) {
            return false;
        }
        if (description != null && !description.isBlank() && (workspace.getDescription() == null
                || !workspace.getDescription().toLowerCase(Locale.ROOT).contains(description.toLowerCase(Locale.ROOT)))) {
            return false;
        }
        if (state != null && !state.equals(workspace.getState().toString().toUpperCase(Locale.ROOT))) {
            return false;
        }
        if (ownerId != null && !ownerId.equals(workspace.getOwnerId().toString())) {
            return false;
        }
        if (from != null && (workspace.getCreatedAt() == null || workspace.getCreatedAt().isAfter(from))) {
            return false;
        }

        return until == null || (workspace.getCreatedAt() != null && !workspace.getCreatedAt().isBefore(until));
    }

}
