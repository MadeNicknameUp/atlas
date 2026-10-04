package org.atlas.userservice.api.service;

import jakarta.persistence.EntityNotFoundException;
import org.atlas.userservice.api.exception.unit.NotFoundException;
import org.atlas.userservice.store.model.User;
import org.atlas.userservice.store.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //GET
    public User getUserById(UUID id){
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + id));
    }

    // PATCH (displayName, avatarUrl)
    @Transactional
    public User updateProfile(UUID id, User profileFields) {
        User existingUser = getUserById(id);

        if (profileFields.getDisplayName() != null) {
            existingUser.setDisplayName(profileFields.getDisplayName());
        }
        if (profileFields.getAvatarUrl() != null) {
            existingUser.setAvatarUrl(profileFields.getAvatarUrl());
        }

        return userRepository.save(existingUser);
    }

    // PATCH (preferences)
    @Transactional
    public User updatePreferences(UUID id, User preferenceFields) {
        User existingUser = getUserById(id);

        if (preferenceFields.getPreferences() != null) {
            existingUser.setPreferences(preferenceFields.getPreferences());
        }

        return userRepository.save(existingUser);
    }

    // PATCH (notificationPreferences)
    @Transactional
    public User updateNotificationPreferences(UUID id, User notificationFields) {
        User existingUser = getUserById(id);

        if (notificationFields.getNotificationPreferences() != null) {
            existingUser.setNotificationPreferences(notificationFields.getNotificationPreferences());
        }

        return userRepository.save(existingUser);
    }

}
