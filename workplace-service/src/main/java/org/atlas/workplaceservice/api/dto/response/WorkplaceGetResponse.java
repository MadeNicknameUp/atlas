package org.atlas.workplaceservice.api.dto.response;

import org.atlas.workplaceservice.store.model.Member;
import org.atlas.workplaceservice.store.model.Workplace;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record WorkplaceGetResponse(
        UUID workplaceId,
        String name,
        String description,
        UUID ownerId,
        List<UUID> memberIds,
        Instant updatedAt,
        Instant createdAt
) {

    public static WorkplaceGetResponse from(Workplace workplace) {

        return new WorkplaceGetResponse(
                workplace.getId(),
                workplace.getName(),
                workplace.getDescription(),
                workplace.getOwnerId(),
                workplace.getMembers()
                        .stream()
                        .map(Member::getId)
                        .toList(),
                workplace.getUpdatedAt(),
                workplace.getCreatedAt()
        );
    }
}