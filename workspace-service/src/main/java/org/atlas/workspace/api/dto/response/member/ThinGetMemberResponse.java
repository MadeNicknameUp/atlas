package org.atlas.workspace.api.dto.response.member;

import org.atlas.workspace.store.domain.Member;

import java.time.Instant;
import java.util.UUID;

public record ThinGetMemberResponse(
        UUID id,
        UUID userId,
        String role,
        String state,
        Instant joinedAt
) {
    public static ThinGetMemberResponse from(Member member) {

        return new ThinGetMemberResponse(
                member.getId(),
                member.getUserId(),
                member.getRole().toString(),
                member.getState().toString(),
                member.getJoinedAt()
        );

    }
}
