package org.atlas.workspace.api.dto.request;

import java.time.Instant;

public record WorkspaceFilter(
        String name,
        String description,
        String state,
        String ownerId,
        Instant from,
        Instant until
) {
}
