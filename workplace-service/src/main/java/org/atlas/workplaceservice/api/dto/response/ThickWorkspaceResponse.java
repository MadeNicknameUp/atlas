package org.atlas.workplaceservice.api.dto.response;

import org.atlas.workplaceservice.store.model.Member;
import org.atlas.workplaceservice.store.model.Workspace;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ThickWorkspaceResponse(
        UUID workplaceId,
        String name,
        String description,
        UUID ownerId,
        List<UUID> memberIds,
        Instant updatedAt,
        Instant createdAt
) {

    public static ThickWorkspaceResponse from(Workspace workspace) {

        return new ThickWorkspaceResponse(
                workspace.getId(),
                workspace.getName(),
                workspace.getDescription(),
                workspace.getOwnerId(),
                workspace.getMembers()
                        .stream()
                        .map(Member::getId)
                        .toList(),
                workspace.getUpdatedAt(),
                workspace.getCreatedAt()
        );
    }
}