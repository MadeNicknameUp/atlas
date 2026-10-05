package org.atlas.userservice.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ProfileUpdateRequest(
        @JsonProperty("displayName") String displayName,
        @JsonProperty("avatarUrl") String avatarUrl
) {}
