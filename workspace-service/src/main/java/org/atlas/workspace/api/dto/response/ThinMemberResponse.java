package org.atlas.workspace.api.dto.response;

import org.atlas.workspace.store.model.Member;

import java.time.Instant;
import java.util.UUID;

public record ThinMemberResponse(
        UUID id,
        UUID userId,
        String role,
        String state,
        Instant joinedAt
) {
    public static ThinMemberResponse from(Member member) {

        return new ThinMemberResponse(
                member.getId(),
                member.getUserId(),
                member.getRole().toString(),
                member.getState().toString(),
                member.getJoinedAt()
        );

    }
}

