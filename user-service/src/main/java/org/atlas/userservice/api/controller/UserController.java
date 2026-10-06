package org.atlas.userservice.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.atlas.userservice.api.dto.request.UpdateProfileRequest;
import org.atlas.userservice.api.dto.response.UserResponse;
import org.atlas.userservice.api.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users/me")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserResponse> getMe(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(UserResponse.from(userService.getBySubject(jwt.getSubject())));
    }

    @PatchMapping("/profile")
    public ResponseEntity<UserResponse> updateProfile(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        return ResponseEntity.ok(UserResponse.from(
                userService.updateProfile(jwt.getSubject(), request)));
    }

    @PatchMapping("/preferences")
    public ResponseEntity<UserResponse> updatePreferences(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody Map<String, Object> request
    ) {
        return ResponseEntity.ok(UserResponse.from(
                userService.updatePreferences(jwt.getSubject(), request)));
    }

    @PatchMapping("/notification-preferences")
    public ResponseEntity<UserResponse> updateNotificationPreferences(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody Map<String, Object> request
    ) {
        return ResponseEntity.ok(UserResponse.from(
                userService.updateNotificationPreferences(jwt.getSubject(), request)));
    }
}
