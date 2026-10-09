package org.atlas.workspace.api.dto.response.workspace;

import org.atlas.workspace.store.domain.Member;
import org.atlas.workspace.store.domain.Workspace;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record WorkspaceCreatedResponse(
        UUID workplaceId,
        UUID ownerId,
        List<UUID> memberIds,
        Instant createdAt
) {

    public static WorkspaceCreatedResponse from(Workspace workplace) {

        return new WorkspaceCreatedResponse(
                workplace.getId(),
                workplace.getOwnerId(),
                workplace.getMembers()
                        .stream()
                        .map(Member::getId)
                        .toList(),
                workplace.getCreatedAt()
        );
    }
}
