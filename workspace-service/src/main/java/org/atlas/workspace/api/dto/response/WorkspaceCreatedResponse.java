package org.atlas.workspace.api.dto.response;

import org.atlas.workspace.store.model.Member;
import org.atlas.workspace.store.model.Workspace;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record WorkspaceCreatedResponse(
        UUID workspaceId,
        String name,
        String iconUrl,
        String description,
        UUID ownerId,
        List<UUID> memberIds,
        Instant createdAt
) {

    public static WorkspaceCreatedResponse from(Workspace workspace) {

        return new WorkspaceCreatedResponse(
                workspace.getId(),
                workspace.getName(),
                workspace.getIconUrl(),
                workspace.getDescription(),
                workspace.getOwnerId(),
                workspace.getMembers()
                        .stream()
                        .map(Member::getId)
                        .toList(),
                workspace.getCreatedAt()
        );
    }
}
