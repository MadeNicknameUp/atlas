package org.atlas.workspace;

import org.atlas.workspace.api.dto.request.WorkspaceCreateRequest;
import org.atlas.workspace.store.repository.WorkspaceRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;


import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class WorkspaceServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WorkspaceRepository workspaceRepository;

    @BeforeEach
    void setUp() {
        workspaceRepository.deleteAll();
    }

    @AfterAll
    static void cleanUp(@Autowired WorkspaceRepository workspaceRepository) {
        workspaceRepository.deleteAll();
    }

    @Test
    void workspaceMayBeCreated() throws Exception {

        // Arrange
        String userId = "3adc4dd4-b5c0-4345-bf83-c44ef92188b9";

        WorkspaceCreateRequest request = new WorkspaceCreateRequest(
                "name",
                "icon_url",
                "description"
        );

        // Act
        mockMvc.perform(post("/api/v1/workspaces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(request.name()))
                .andExpect(jsonPath("$.icon_url").value(request.iconUrl()))
                .andExpect(jsonPath("$.description").value(request.description()))
                .andExpect(jsonPath("$.owner_id").value(userId));

        // Assert
        assertThat(workspaceRepository.count()).isEqualTo(1);
    }

    @Test
    void workspaceMayBeCreatedValidationException() throws Exception {

        // Arrange
        WorkspaceCreateRequest request = new WorkspaceCreateRequest(
                "verylongverylongverylongverylongverylongverylongverylongverylongverylongverylongverylongverylongverylongverylongverylongverylongverylongverylongverylongverylongverylongverylongverylongverylongverylongverylong",
                "icon_url",
                "description"
        );

        // Act
        mockMvc.perform(post("/api/v1/workspaces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("ValidationException: Provided argument violates validation constraints."))
                .andExpect(jsonPath("$.path").value("/api/v1/workspaces"))
                .andExpect(jsonPath("$.details").isNotEmpty());

        // Assert
        assertThat(workspaceRepository.count()).isEqualTo(0);
    }

}


