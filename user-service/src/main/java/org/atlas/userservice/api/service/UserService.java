package org.atlas.userservice.api.service;

import jakarta.persistence.EntityNotFoundException;
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

    // POST
    @Transactional
    public User createUser(User user){
        return userRepository.save(user);
    }

    //GET
    public User getUserById(UUID id){
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));
    }

    //PATCH
    @Transactional
    public User updateUser(UUID id, User updatedFields) {
        User existingUser = getUserById(id);
        if (updatedFields.getDisplayName() != null) {
            existingUser.setDisplayName(updatedFields.getDisplayName());
        }
        if (updatedFields.getAvatarUrl() != null) {
            existingUser.setAvatarUrl(updatedFields.getAvatarUrl());
        }
        if (updatedFields.getPreferences() != null) {
            existingUser.setPreferences(updatedFields.getPreferences());
        }
        if (updatedFields.getNotificationPreferences() != null) {
            existingUser.setNotificationPreferences(updatedFields.getNotificationPreferences());
        }

        return userRepository.save(existingUser);
    }

    //DELETE
    @Transactional
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }
}
