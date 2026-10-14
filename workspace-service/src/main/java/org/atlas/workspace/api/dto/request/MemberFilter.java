package org.atlas.workspace.api.dto.request;

import org.atlas.workspace.store.model.Member;

import java.time.Instant;

public record MemberFilter(
        String role,
        String state,
        String invitedBy,
        String removedBy,
        Instant joinedFrom,
        Instant joinedUntil
) implements SearchFilter<Member> {



}
