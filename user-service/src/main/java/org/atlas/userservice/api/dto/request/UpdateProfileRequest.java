package org.atlas.userservice.api.dto.request;

import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @Size(max = 255) String fullName,
        @Size(max = 100) String displayName,
        @Size(max = 50)  String username,
        @Size(max = 1000) String about,
        @Size(max = 100) String position
) {}
