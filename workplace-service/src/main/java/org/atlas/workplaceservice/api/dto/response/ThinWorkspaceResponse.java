package org.atlas.workplaceservice.api.dto.response;

import org.atlas.workplaceservice.store.model.Workspace;

import java.time.Instant;
import java.util.UUID;

public record ThinWorkspaceResponse(
        UUID workplaceId,
        String name,
        String description,
        UUID ownerId,
        Instant createdAt
) {

    public static ThinWorkspaceResponse from(Workspace workspace) {
        return new ThinWorkspaceResponse(
                workspace.getId(),
                workspace.getName(),
                workspace.getDescription(),
                workspace.getOwnerId(),
                workspace.getCreatedAt()
        );
    }
}
