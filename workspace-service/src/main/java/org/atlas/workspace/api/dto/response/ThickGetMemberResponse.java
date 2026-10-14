package org.atlas.workspace.api.dto.response;

import org.atlas.workspace.store.model.Member;
import org.atlas.workspace.store.model.MemberState;

public record ThickGetMemberResponse(
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
    public static ThickGetMemberResponse from(Member member) {
        return new ThickGetMemberResponse(
                member.getId().toString(),
                member.getUserId().toString(),
                member.getRole().toString(),
                member.getState().toString(),
                member.getJoinedAt().toString(),
                member.getUpdatedAt().toString(),
                member.getState().equals(MemberState.ACTIVE) ? null : member.getExpelledAt().toString(),
                member.getInvitedBy().getUserId().toString(), // <-- we really need display name or smth?
                member.getUpdatedBy().getUserId().toString(), // <-- we really need display name or smth?
                member.getState().equals(MemberState.ACTIVE) ? null : member.getExpelledBy().getUserId().toString()  // <-- we really need display name or smth?
        );
    }
}

