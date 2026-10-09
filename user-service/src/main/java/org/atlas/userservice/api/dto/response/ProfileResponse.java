package org.atlas.userservice.api.dto.response;

import org.atlas.userservice.store.model.Profile;

public record ProfileResponse(
        String fullName, String displayName, String username, String about, String position
) {
    public static ProfileResponse from(Profile p) {
        if (p == null) return null;
        return new ProfileResponse(p.getFullName(), p.getDisplayName(),
                p.getUsername(), p.getAbout(), p.getPosition());
    }
}
