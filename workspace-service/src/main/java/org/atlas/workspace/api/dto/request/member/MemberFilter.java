package org.atlas.workspace.api.dto.request.member;

import org.atlas.workspace.store.domain.Member;

import java.time.Instant;
import java.util.Locale;

public record MemberFilter(
        String role,
        String state,
        String invitedBy,
        String removedBy,
        Instant joinedFrom,
        Instant joinedUntil
) implements org.atlas.workspace.api.dto.request.SearchFilter<Member> {

    public boolean applyOn(Member member) {
        if (role != null && !role.isBlank() && (member.getRole() == null
                || !member.getRole().toString().toLowerCase(Locale.ROOT).contains(role.toLowerCase(Locale.ROOT)))) {
            return false;
        }
        if (state != null && !state.isBlank() && (member.getState() == null
                || !member.getState().toString().toLowerCase(Locale.ROOT).contains(state.toLowerCase(Locale.ROOT)))) {
            return false;
        }
        if (state != null && !state.equals(member.getState().toString().toUpperCase(Locale.ROOT))) {
            return false;
        }
        if (invitedBy != null && !invitedBy.equals(member.getInvitedBy().getId().toString())) {
            return false;
        }
        if (removedBy != null && !removedBy.equals(member.getRemovedBy().getId().toString())) {
            return false;
        }
        if (joinedFrom != null && (member.getJoinedAt() == null || member.getJoinedAt().isAfter(joinedFrom))) {
            return false;
        }

        return joinedUntil == null || (member.getJoinedAt() != null && !member.getJoinedAt().isBefore(joinedUntil));
    }

}
