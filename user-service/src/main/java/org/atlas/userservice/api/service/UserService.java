package org.atlas.userservice.api.service;

import lombok.RequiredArgsConstructor;
import org.atlas.userservice.api.dto.request.UpdateProfileRequest;
import org.atlas.userservice.store.model.Profile;
import org.atlas.userservice.store.model.User;
import org.atlas.userservice.store.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User getBySubject(String subject) {
        return userRepository.findByIdentitySubject(subject).orElseThrow(() -> new UserNotFoundException(subject));
    }

    @Transactional
    public User updateProfile(String subject, UpdateProfileRequest req) {
        User user = getBySubject(subject);

        Profile profile = user.getProfile();
        if (profile == null) {
            profile = new Profile();
            profile.setUser(user);
            user.setProfile(profile);
        }

        if (req.fullName() != null)    profile.setFullName(req.fullName());
        if (req.displayName() != null) profile.setDisplayName(req.displayName());
        if (req.username() != null)    profile.setUsername(req.username());
        if (req.about() != null)       profile.setAbout(req.about());
        if (req.position() != null)    profile.setPosition(req.position());

        return user;
    }

    @Transactional
    public User updatePreferences(String subject, Map<String, Object> patch) {
        User user = getBySubject(subject);
        user.setPreferences(merge(user.getPreferences(), patch));
        return user;
    }

    @Transactional
    public User updateNotificationPreferences(String subject, Map<String, Object> patch) {
        User user = getBySubject(subject);
        user.setNotificationPreferences(merge(user.getNotificationPreferences(), patch));
        return user;
    }

    private Map<String, Object> merge(Map<String, Object> current, Map<String, Object> patch) {
        Map<String, Object> result = new HashMap<>(current);
        patch.forEach((k, v) -> {
            if (v == null) result.remove(k);
            else result.put(k, v);
        });
        return result;   // новая Map, чтобы Hibernate точно увидел изменение
    }
}
