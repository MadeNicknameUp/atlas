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

    @PatchMapping("/me/profile")
    public ResponseEntity<User> updateProfile(Principal principal, @RequestBody User profileFields) {
        UUID userId = extractUserId(principal);
        return ResponseEntity.ok(userService.updateProfile(userId, profileFields));
    }

    @PatchMapping("/me/preferences")
    public ResponseEntity<User> updatePreferences(Principal principal, @RequestBody User preferences) {
        UUID userId = extractUserId(principal);
        return ResponseEntity.ok(userService.updatePreferences(userId, preferences));
    }

    @PatchMapping("/me/notificationPreferences")
    public ResponseEntity<User> updateNotificationPreferences(Principal principal, @RequestBody User preferences) {
        UUID userId = extractUserId(principal);
        return ResponseEntity.ok(userService.updateNotificationPreferences(userId, preferences));
    }

    private UUID extractUserId(Principal principal) {
        //principal.getName() returns UUID from Keycloak's token
        String userIdStr = (principal != null && principal.getName() != null)
                ? principal.getName()
                : "123e4567-e89b-12d3-a456-426614174000"; // DEFAULT UUID
        return UUID.fromString(userIdStr);
    }
}
