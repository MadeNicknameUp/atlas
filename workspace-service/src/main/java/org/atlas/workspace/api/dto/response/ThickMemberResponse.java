package org.atlas.workspace.api.dto.response;

import org.atlas.workspace.domain.model.Member;
import org.atlas.workspace.domain.model.MemberState;

public record ThickMemberResponse(
        String id,
        String userId,
        String role,
        String state,
        String joinedAt,
        String updatedAt,
        String removedAt,
        String invitedBy,
        String updatedBy,
        String removedBy
) {
    public static ThickMemberResponse from(Member member) {
        return new ThickMemberResponse(
                member.getId().toString(),
                member.getUserId().toString(),
                member.getRole().toString(),
                member.getState().toString(),
                member.getJoinedAt().toString(),
                member.getUpdatedAt().toString(),
                member.getState().equals(MemberState.ACTIVE) ? null : member.getExpelledAt().toString(),
                member.getInvitedBy().getUserId().toString(),
                member.getUpdatedBy().getUserId().toString(),
                member.getState().equals(MemberState.ACTIVE) ? null : member.getExpelledBy().getUserId().toString()
        );
    }
}

