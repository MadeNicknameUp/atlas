package org.atlas.workspace.api.dto.response;

import org.atlas.workspace.store.model.Member;
import org.atlas.workspace.store.model.Workspace;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ThickWorkspaceResponse(
        UUID workplaceId,
        String name,
        String iconUrl,
        String description,
        String state,
        UUID ownerId,
        List<UUID> memberIds,
        Instant updatedAt,
        Instant createdAt
) {

    public static ThickWorkspaceResponse from(Workspace workspace) {

        return new ThickWorkspaceResponse(
                workspace.getId(),
                workspace.getName(),
                workspace.getIconUrl(),
                workspace.getDescription(),
                workspace.getState().toString(),
                workspace.getOwner().getUserId(),
                workspace.getMembers()
                        .stream()
                        .map(Member::getId)
                        .toList(),
                workspace.getUpdatedAt(),
                workspace.getCreatedAt()
        );
    }
}