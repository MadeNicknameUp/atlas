package org.atlas.workspace.api.dto.response;

import org.atlas.workspace.store.model.Member;
import org.atlas.workspace.store.model.Workspace;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record WorkspaceCreatedResponse(
        UUID workplaceId,
        String name,
        String iconUrl,
        String description,
        UUID ownerId,
        List<UUID> memberIds,
        Instant createdAt
) {

    public static WorkspaceCreatedResponse from(Workspace workplace) {

        return new WorkspaceCreatedResponse(
                workplace.getId(),
                workplace.getName(),
                workplace.getIconUrl(),
                workplace.getDescription(),
                workplace.getOwner().getUserId(),
                workplace.getMembers()
                        .stream()
                        .map(Member::getId)
                        .toList(),
                workplace.getCreatedAt()
        );
    }
}
