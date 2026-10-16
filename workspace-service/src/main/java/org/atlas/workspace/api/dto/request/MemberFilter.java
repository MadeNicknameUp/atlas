package org.atlas.workspace.api.dto.request;

import java.time.Instant;

public record MemberFilter(
        String role,
        String state,
        String invitedBy,
        String expelledBy,
        Instant joinedFrom,
        Instant joinedUntil
) implements SearchFilter {

}
