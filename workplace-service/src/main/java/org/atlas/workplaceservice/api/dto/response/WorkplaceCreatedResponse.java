package org.atlas.workplaceservice.api.dto.response;

import org.atlas.workplaceservice.store.model.Member;
import org.atlas.workplaceservice.store.model.Workplace;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record WorkplaceCreatedResponse(
        UUID workplaceId,
        UUID ownerId,
        List<UUID> memberIds,
        Instant createdAt
) {

    public static WorkplaceCreatedResponse from(Workplace workplace) {

        return new WorkplaceCreatedResponse(
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
