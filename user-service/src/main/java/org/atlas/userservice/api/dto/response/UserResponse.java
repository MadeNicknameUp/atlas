package org.atlas.userservice.api.dto.response;

import org.atlas.userservice.store.model.User;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record UserResponse(
        UUID id,
        boolean active,
        Map<String, Object> preferences,
        Map<String, Object> notificationPreferences,
        ProfileResponse profile,
        Instant createdAt,
        Instant updatedAt
) {
    public static UserResponse from(User u) {
        return new UserResponse(u.getId(), u.isActive(), u.getPreferences(),
                u.getNotificationPreferences(), ProfileResponse.from(u.getProfile()),
                u.getCreatedAt(), u.getUpdatedAt());
    }
}
