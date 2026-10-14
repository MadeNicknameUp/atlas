package org.atlas.workspace.config;

import org.openapitools.jackson.nullable.JsonNullableJackson3Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsonNullableConfig {

    @Bean
    public JsonNullableJackson3Module jsonNullableModule() {
        return new JsonNullableJackson3Module();
    }
}
