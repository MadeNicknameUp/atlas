package org.atlas.workspace;

import org.atlas.workspace.api.dto.request.WorkspaceCreateRequest;
import org.atlas.workspace.store.model.Workspace;
import org.atlas.workspace.store.model.WorkspaceState;
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


import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

        // Act & Assert
        mockMvc.perform(post("/api/v1/workspaces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(request.name()))
                .andExpect(jsonPath("$.icon_url").value(request.iconUrl()))
                .andExpect(jsonPath("$.description").value(request.description()))
                .andExpect(jsonPath("$.owner_id").value(userId));

        assertThat(workspaceRepository.count()).isEqualTo(1);
    }

    @Test
    void workspaceMayBeCreatedValidationException() throws Exception {

        // Arrange
        WorkspaceCreateRequest request = new WorkspaceCreateRequest(
                """
                        verylongverylongverylongverylongverylongverylongverylongverylongverylongverylongv
                        erylongverylongverylongverylongverylongverylongverylongverylongverylongverylongve
                        rylongverylongverylongverylongverylongverylong
                        """,
                "icon_url",
                "description"
        );

        // Act & Assert
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

    @Test
    void workspaceMayBeFetched() throws Exception {

        // Arrange
        UUID ownerId = UUID.fromString("3adc4dd4-b5c0-4345-bf83-c44ef92188b9");

        Workspace workspace = workspaceRepository.save(Workspace.create(
                "name",
                "iconUrl",
                "description",
                ownerId)
        );

        // Act & Assert
        mockMvc.perform(get("/api/v1/workspaces/%s".formatted(workspace.getId()))
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.workspace_id").value(workspace.getId().toString()))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.icon_url").value("iconUrl"))
                .andExpect(jsonPath("$.state").value(WorkspaceState.ACTIVE.toString()))
                .andExpect(jsonPath("$.owner_id").value(ownerId.toString()))
                .andExpect(jsonPath("$.icon_url").value("iconUrl"))
                .andExpect(jsonPath("$.description").value("description"));

        // Assert
        assertThat(workspaceRepository.count()).isEqualTo(1);
        assertThat(workspaceRepository.findById(workspace.getId())).isPresent();
    }

    @Test
    void workspaceNotFoundAndDatabaseIsIntact() throws Exception {

        // Arrange
        UUID ownerId = UUID.fromString("3adc4dd4-b5c0-4345-bf83-c44ef92188b9");

        Workspace workspace = workspaceRepository.save(Workspace.create(
                "name",
                "iconUrl",
                "description",
                ownerId)
        );

        UUID invalidWorkspaceId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(get("/api/v1/workspaces/%s".formatted(invalidWorkspaceId))
                ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Workspace with id: %s not found.".formatted(invalidWorkspaceId)))
                .andExpect(jsonPath("$.path").value("/api/v1/workspaces/%s".formatted(invalidWorkspaceId)));

        // Assert
        assertThat(workspaceRepository.count()).isEqualTo(1);
        assertThat(workspaceRepository.findById(workspace.getId())).isPresent();
    }

}


