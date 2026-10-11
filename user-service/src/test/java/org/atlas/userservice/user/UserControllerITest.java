package org.atlas.userservice.user;

import org.atlas.userservice.store.model.User;
import org.atlas.userservice.store.repository.UserRepository;
import org.atlas.userservice.templates.AbstractITestWithDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerITest extends AbstractITestWithDatabase {

    private static final String BASE = "/api/v1/users/me";
    private static final String SUBJECT_HEADER = "X-Subject";
    private static final String SUBJECT = "test-subject";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();   // cascade ALL удалит и профили
    }

    // ---------- GET /me ----------

    @Test
    void getMeReturnsUserWithoutProfile() throws Exception {
        // Arrange
        User user = saveUser(SUBJECT);

        // Act & Assert
        mockMvc.perform(get(BASE).header(SUBJECT_HEADER, SUBJECT))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId().toString()))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.profile").value(nullValue()))
                .andExpect(jsonPath("$.preferences").isEmpty())
                .andExpect(jsonPath("$.notification_preferences").isEmpty())
                .andExpect(jsonPath("$.created_at").isNotEmpty())
                .andExpect(jsonPath("$.updated_at").isNotEmpty());
    }

    @Test
    void getMeForUnknownUserReturns404InUnifiedFormat() throws Exception {
        mockMvc.perform(get(BASE).header(SUBJECT_HEADER, "ghost"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("User not found: subject=ghost"))
                .andExpect(jsonPath("$.path").value(BASE));

        assertThat(userRepository.count()).isZero();   // GET ничего не создаёт
    }

    @Test
    void getMeForDeactivatedUserStillWorksAndShowsActiveFalse() throws Exception {
        // Arrange
        User user = new User(SUBJECT);
        user.deactivate();
        userRepository.save(user);

        // Act & Assert
        mockMvc.perform(get(BASE).header(SUBJECT_HEADER, SUBJECT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }

    // ---------- PATCH /me/profile ----------

    @Test
    void profileUpdateChangesOnlyProvidedFields() throws Exception {
        // Arrange
        saveUser(SUBJECT);

        // Act & Assert
        patchJson(BASE + "/profile", """
                {"full_name": "Maksym", "username": "maks"}
                """)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profile.full_name").value("Maksym"))
                .andExpect(jsonPath("$.profile.username").value("maks"));

        patchJson(BASE + "/profile", """
                {"about": "hello"}
                """)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profile.full_name").value("Maksym"))   // не тронуто
                .andExpect(jsonPath("$.profile.username").value("maks"))      // не тронуто
                .andExpect(jsonPath("$.profile.about").value("hello"));

        assertThat(reload().getProfile().getFullName()).isEqualTo("Maksym");
        assertThat(reload().getProfile().getAbout()).isEqualTo("hello");
    }

    @Test
    void profileUpdateWithExplicitNullClearsOnlyThatField() throws Exception {
        // Arrange
        saveUser(SUBJECT);
        patchJson(BASE + "/profile", """
                {"full_name": "Maksym", "username": "maks"}
                """).andExpect(status().isOk());

        // Act & Assert
        patchJson(BASE + "/profile", """
                {"full_name": null}
                """)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profile.full_name").value(nullValue()))
                .andExpect(jsonPath("$.profile.username").value("maks"));

        assertThat(reload().getProfile().getFullName()).isNull();
        assertThat(reload().getProfile().getUsername()).isEqualTo("maks");
    }

    @Test
    void profileUpdateWithTooLongUsernameReturns400AndChangesNothing() throws Exception {
        // Arrange
        saveUser(SUBJECT);
        String json = "{\"username\": \"" + "a".repeat(51) + "\"}";

        // Act & Assert
        patchJson(BASE + "/profile", json)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value(containsString("username")))
                .andExpect(jsonPath("$.path").value(BASE + "/profile"));

        assertThat(reload().getProfile()).isNull();
    }

    @Test
    void profileUpdateOfDeactivatedUserReturns403AndChangesNothing() throws Exception {
        // Arrange
        User user = new User(SUBJECT);
        user.deactivate();
        user = userRepository.save(user);

        // Act & Assert
        patchJson(BASE + "/profile", """
                {"full_name": "Maksym"}
                """)
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403))
                .andExpect(jsonPath("$.message").value(containsString(user.getId().toString())))
                .andExpect(jsonPath("$.path").value(BASE + "/profile"));

        assertThat(reload().getProfile()).isNull();
    }

    // ---------- PATCH /me/preferences, /me/notification-preferences ----------

    @Test
    void preferencesAreMergedAndNullRemovesKey() throws Exception {
        // Arrange
        saveUser(SUBJECT);
        patchJson(BASE + "/preferences", """
                {"theme": "dark", "lang": "uk"}
                """).andExpect(status().isOk());

        // Act & Assert
        patchJson(BASE + "/preferences", """
                {"lang": null, "font_size": 14}
                """)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preferences.theme").value("dark"))
                .andExpect(jsonPath("$.preferences.font_size").value(14))
                .andExpect(jsonPath("$.preferences.lang").doesNotExist());

        assertThat(reload().getPreferences())
                .containsEntry("theme", "dark")
                .containsEntry("font_size", 14)
                .doesNotContainKey("lang");
    }

    @Test
    void notificationPreferencesUpdateDoesNotTouchPreferences() throws Exception {
        // Arrange
        saveUser(SUBJECT);
        patchJson(BASE + "/preferences", """
                {"theme": "dark"}
                """).andExpect(status().isOk());

        // Act & Assert
        patchJson(BASE + "/notification-preferences", """
                {"email": true, "push": false}
                """)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notification_preferences.email").value(true))
                .andExpect(jsonPath("$.notification_preferences.push").value(false))
                .andExpect(jsonPath("$.preferences.theme").value("dark"));
    }

    @Test
    void preferencesUpdateWithoutBodyReturns400() throws Exception {
        saveUser(SUBJECT);

        mockMvc.perform(patch(BASE + "/preferences")
                        .header(SUBJECT_HEADER, SUBJECT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Request body is missing or is not valid JSON"))
                .andExpect(jsonPath("$.path").value(BASE + "/preferences"));
    }

    // ---------- общий формат ошибок ----------

    @Test
    void unknownPathReturns404InUnifiedFormat() throws Exception {
        mockMvc.perform(get("/api/v1/nope"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.path").value("/api/v1/nope"));
    }

    @Test
    void unsupportedMethodReturns405InUnifiedFormat() throws Exception {
        mockMvc.perform(post(BASE).header(SUBJECT_HEADER, SUBJECT))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.code").value(405))
                .andExpect(jsonPath("$.path").value(BASE));
    }

    // ---------- helpers ----------

    private User saveUser(String subject) {
        return userRepository.save(new User(subject));
    }

    /** Перечитываем из БД (EntityGraph подтягивает profile), чтобы проверять реальное состояние. */
    private User reload() {
        return userRepository.findByIdentitySubject(SUBJECT).orElseThrow();
    }

    private ResultActions patchJson(String url, String json) throws Exception {
        return mockMvc.perform(patch(url)
                        .header(SUBJECT_HEADER, SUBJECT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print());
    }
}
