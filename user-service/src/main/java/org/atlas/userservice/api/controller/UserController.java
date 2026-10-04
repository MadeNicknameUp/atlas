package org.atlas.userservice.api.controller;

import org.atlas.userservice.api.service.UserService;
import org.atlas.userservice.store.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable UUID userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{userId}/profile")
    public ResponseEntity<User> updateProfile(
            @PathVariable UUID userId,
            @RequestBody User profileFields
    ) {
        return ResponseEntity.ok(userService.updateProfile(userId, profileFields));
    }

    @PatchMapping("/{userId}/preferences")
    public ResponseEntity<User> updatePreferences(
            @PathVariable UUID userId,
            @RequestBody User preferences
    ) {
        return ResponseEntity.ok(userService.updatePreferences(userId, preferences));
    }

    @PatchMapping("/{userId}/notificationPreferences")
    public ResponseEntity<User> updateNotificationPreferences(
            @PathVariable UUID userId,
            @RequestBody User preferences
    ) {
        return ResponseEntity.ok(userService.updateNotificationPreferences(userId, preferences));
    }

//    private UUID extractUserId(Principal principal) {
//        //principal.getName() returns UUID from Keycloak's token
//        String userIdStr = (principal != null && principal.getName() != null)
//                ? principal.getName()
//                : "123e4567-e89b-12d3-a456-426614174000"; // DEFAULT UUID
//        return UUID.fromString(userIdStr);
//    }
}
