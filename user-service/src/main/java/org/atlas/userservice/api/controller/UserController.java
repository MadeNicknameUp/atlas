package org.atlas.userservice.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.atlas.userservice.api.dto.request.UpdateProfileRequest;
import org.atlas.userservice.api.dto.response.UserResponse;
import org.atlas.userservice.api.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users/me")
@RequiredArgsConstructor
public class UserController {

    // TODO(auth): заменить заголовок на @AuthenticationPrincipal Jwt jwt -> jwt.getSubject(),
    //  когда появится identity-провайдер. Остальной код менять не нужно.
    private static final String SUBJECT_HEADER = "X-Subject";
    private static final String DEV_SUBJECT = "dev-user";

    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserResponse> getMe(
            @RequestHeader(value = SUBJECT_HEADER, defaultValue = DEV_SUBJECT) String subject
    ) {
        return ResponseEntity.ok(UserResponse.from(userService.getMe(subject)));
    }

    @PatchMapping("/profile")
    public ResponseEntity<UserResponse> updateProfile(
            @RequestHeader(value = SUBJECT_HEADER, defaultValue = DEV_SUBJECT) String subject,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        return ResponseEntity.ok(UserResponse.from(userService.updateProfile(subject, request)));
    }

    @PatchMapping("/preferences")
    public ResponseEntity<UserResponse> updatePreferences(
            @RequestHeader(value = SUBJECT_HEADER, defaultValue = DEV_SUBJECT) String subject,
            @RequestBody Map<String, Object> request
    ) {
        return ResponseEntity.ok(UserResponse.from(userService.updatePreferences(subject, request)));
    }

    @PatchMapping("/notification-preferences")
    public ResponseEntity<UserResponse> updateNotificationPreferences(
            @RequestHeader(value = SUBJECT_HEADER, defaultValue = DEV_SUBJECT) String subject,
            @RequestBody Map<String, Object> request
    ) {
        return ResponseEntity.ok(UserResponse.from(userService.updateNotificationPreferences(subject, request)));
    }
}
