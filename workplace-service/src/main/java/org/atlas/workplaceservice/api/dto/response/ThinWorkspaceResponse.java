package org.atlas.workplaceservice.api.dto.response;

import org.atlas.workplaceservice.store.model.Workspace;

import java.time.Instant;
import java.util.UUID;

public record ThinWorkspaceResponse(
        UUID workplaceId,
        String name,
        String iconUrl,
        String description,
        String state,
        UUID ownerId,
        Instant createdAt
) {

    public static ThinWorkspaceResponse from(Workspace workspace) {
        return new ThinWorkspaceResponse(
                workspace.getId(),
                workspace.getName(),
                workspace.getIconUrl(),
                workspace.getDescription(),
                workspace.getState().toString(),
                workspace.getOwnerId(),
                workspace.getCreatedAt()
        );
    }
}
