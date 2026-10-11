package org.atlas.userservice.api.dto;

import org.atlas.userservice.api.dto.request.UpdateProfileRequest;
import org.atlas.userservice.api.dto.request.UpdateProfileRequest.Field;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;

/** Быстрый unit-тест без Spring и Docker: проверяет, что мы отличаем «поля нет» от «поле = null». */
class UpdateProfileRequestTest {

    private final ObjectMapper mapper = JsonMapper.builder().build();

    @Test
    void absentFieldIsNotMarkedAsProvided() {
        UpdateProfileRequest req = mapper.readValue("{}", UpdateProfileRequest.class);

        assertThat(req.has(Field.FULL_NAME)).isFalse();
        assertThat(req.has(Field.ABOUT)).isFalse();
    }

    @Test
    void explicitNullIsMarkedAsProvided() {
        UpdateProfileRequest req = mapper.readValue("{\"about\": null}", UpdateProfileRequest.class);

        assertThat(req.has(Field.ABOUT)).isTrue();
        assertThat(req.getAbout()).isNull();
        assertThat(req.has(Field.FULL_NAME)).isFalse();
    }

    @Test
    void valueIsMarkedAsProvided() {
        UpdateProfileRequest req = mapper.readValue("{\"fullName\": \"Maksym\"}", UpdateProfileRequest.class);

        assertThat(req.has(Field.FULL_NAME)).isTrue();
        assertThat(req.getFullName()).isEqualTo("Maksym");
    }
}