package org.atlas.workspace.workspace;

import org.atlas.workspace.api.dto.request.WorkspaceCreateRequest;
import org.atlas.workspace.api.dto.request.WorkspaceUpdateRequest;
import org.atlas.workspace.store.model.Workspace;
import org.atlas.workspace.store.model.WorkspaceState;
import org.atlas.workspace.store.repository.WorkspaceRepository;
import org.atlas.workspace.templates.AbstractITestWithDatabase;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class WorkspaceServiceApplicationTests extends AbstractITestWithDatabase {

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
                .andExpect(jsonPath("$.name").value(workspace.getName()))
                .andExpect(jsonPath("$.icon_url").value(workspace.getIconUrl()))
                .andExpect(jsonPath("$.state").value(WorkspaceState.ACTIVE.toString()))
                .andExpect(jsonPath("$.owner_id").value(ownerId.toString()))
                .andExpect(jsonPath("$.description").value(workspace.getDescription()));

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

        assertThat(workspaceRepository.count()).isEqualTo(1);
        assertThat(workspaceRepository.findById(workspace.getId())).isPresent();
    }

    @Test
    void userHasNoWorkspaces() throws Exception {

        // Act & Assert
        mockMvc.perform(get("/api/v1/workspaces")
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

    }

    // filter testing
    @Test
    void userHasManyWorkspacesButOnly25DisplayedAtATimeByDefault() throws Exception {

        // Arrange
        UUID ownerId = UUID.fromString("3adc4dd4-b5c0-4345-bf83-c44ef92188b9");

        List<Workspace> workspaces = new ArrayList<>(38);

        for (int i = 0; i < 38; ++i) {
            workspaces.add(Workspace.create(
                    "name " + i,
                    "iconUrl",
                    "description",
                    ownerId)
            );
        }

        workspaceRepository.saveAll(workspaces);

        // Act & Assert
        mockMvc.perform(get("/api/v1/workspaces"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(25)));

        assertThat(workspaceRepository.count()).isEqualTo(38);
    }

    @Test
    void userHasManyWorkspacesButUserCanSelectHowManyDisplayedAtATime() throws Exception {

        // Arrange
        UUID ownerId = UUID.fromString("3adc4dd4-b5c0-4345-bf83-c44ef92188b9");

        List<Workspace> workspaces = new ArrayList<>(38);

        for (int i = 0; i < 38; ++i) {
            workspaces.add(Workspace.create(
                    "name " + i,
                    "iconUrl",
                    "description",
                    ownerId)
            );
        }

        workspaceRepository.saveAll(workspaces);

        Integer pageSize = 32;

        // Act & Assert
        mockMvc.perform(get("/api/v1/workspaces")
                        .param("page_size", pageSize.toString())
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(pageSize)));

        assertThat(workspaceRepository.count()).isEqualTo(38);
    }

    @Test
    void userHasManyWorkspacesButUserCanSelectHowManyDisplayedAtATimeAndPaginationIsCorrect() throws Exception {

        // Arrange
        UUID ownerId = UUID.fromString("3adc4dd4-b5c0-4345-bf83-c44ef92188b9");

        int totalWorkspaces = 38;

        List<Workspace> workspaces = new ArrayList<>(totalWorkspaces);

        for (int i = 0; i < totalWorkspaces; ++i) {
            workspaces.add(Workspace.create(
                    "name " + i,
                    "iconUrl",
                    "description",
                    ownerId)
            );
        }

        workspaceRepository.saveAll(workspaces);

        int page = 1;
        int pageSize = 32;

        // Act & Assert
        mockMvc.perform(get("/api/v1/workspaces")
                        .param("page_size", Integer.toString(totalWorkspaces - pageSize * page))
                        .param("page", Integer.toString(page))
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(totalWorkspaces - pageSize * page)));

        assertThat(workspaceRepository.count()).isEqualTo(totalWorkspaces);
    }

    @Test
    void userHasManyWorkspacesAndCanFilterThemByName() throws Exception {

        // Arrange
        UUID ownerId = UUID.fromString("3adc4dd4-b5c0-4345-bf83-c44ef92188b9");

        int totalWorkspaces = 38;

        List<Workspace> workspaces = new ArrayList<>(totalWorkspaces);

        for (int i = 0; i < totalWorkspaces; ++i) {
            workspaces.add(Workspace.create(
                    "name " + i,
                    "iconUrl",
                    "description",
                    ownerId)
            );
        }

        workspaceRepository.saveAll(workspaces);

        // Act & Assert
        mockMvc.perform(get("/api/v1/workspaces")
                        .param("name", "name 0")
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$[0].workspace_id").value(workspaces.getFirst().getId().toString()))
                .andExpect(jsonPath("$[0].name").value(workspaces.getFirst().getName()))
                .andExpect(jsonPath("$[0].icon_url").value(workspaces.getFirst().getIconUrl()))
                .andExpect(jsonPath("$[0].state").value(WorkspaceState.ACTIVE.toString()))
                .andExpect(jsonPath("$[0].owner_id").value(ownerId.toString()))
                .andExpect(jsonPath("$[0].description").value(workspaces.getFirst().getDescription()));

        assertThat(workspaceRepository.count()).isEqualTo(totalWorkspaces);
    }

    @Test
    void userHasManyWorkspacesAndCanFilterThemByNameAndPaginationWorks() throws Exception {

        // Arrange
        UUID ownerId = UUID.fromString("3adc4dd4-b5c0-4345-bf83-c44ef92188b9");

        int totalWorkspaces = 38;

        List<Workspace> workspaces = new ArrayList<>(totalWorkspaces);

        for (int i = 0; i < totalWorkspaces; ++i) {
            workspaces.add(Workspace.create(
                    "name " + i,
                    "iconUrl",
                    "description",
                    ownerId)
            );
        }

        workspaceRepository.saveAll(workspaces);

        int page = 1;

        // Act & Assert
        mockMvc.perform(get("/api/v1/workspaces")
                        .param("name", "name")
                        .param("page", Integer.toString(page))
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(totalWorkspaces - 25 * page)));

        assertThat(workspaceRepository.count()).isEqualTo(totalWorkspaces);
    }

    @Test
    void userHasManyWorkspacesAndCanFilterThemByState() throws Exception {

        // Arrange
        UUID ownerId = UUID.fromString("3adc4dd4-b5c0-4345-bf83-c44ef92188b9");

        int totalWorkspaces = 38;

        List<Workspace> workspaces = new ArrayList<>(totalWorkspaces);

        for (int i = 0; i < totalWorkspaces; ++i) {
            workspaces.add(Workspace.create(
                    "name " + i,
                    "iconUrl",
                    "description",
                    ownerId)
            );
        }

        workspaceRepository.saveAll(workspaces);

        // Act & Assert
        mockMvc.perform(get("/api/v1/workspaces")
                        .param("state", WorkspaceState.ACTIVE.toString())
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(25)));

        mockMvc.perform(get("/api/v1/workspaces")
                        .param("state", WorkspaceState.ARCHIVED.toString())
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(0)));

        assertThat(workspaceRepository.count()).isEqualTo(totalWorkspaces);
    }

    @Test
    void workspaceMayBeUpdated() throws Exception {

        // Arrange
        UUID ownerId = UUID.fromString("3adc4dd4-b5c0-4345-bf83-c44ef92188b9");

        Workspace workspace = Workspace.create(
                "name",
                "iconUrl",
                "description",
                ownerId
        );

        WorkspaceUpdateRequest request = new WorkspaceUpdateRequest(
                JsonNullable.of("new name"),
                JsonNullable.undefined(),
                JsonNullable.of("new description")
        );

        workspace = workspaceRepository.save(workspace);

        // Act & Assert
        mockMvc.perform(patch("/api/v1/workspaces/%s".formatted(workspace.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.workspace_id").value(workspace.getId().toString()))
                .andExpect(jsonPath("$.name").value(request.name().get()))
                .andExpect(jsonPath("$.icon_url").value(workspace.getIconUrl()))
                .andExpect(jsonPath("$.state").value(WorkspaceState.ACTIVE.toString()))
                .andExpect(jsonPath("$.owner_id").value(ownerId.toString()))
                .andExpect(jsonPath("$.description").value(request.description().get()));

        assertThat(workspaceRepository.count()).isEqualTo(1);
        assertThat(workspaceRepository.findById(workspace.getId())).isPresent();
        assertThat(workspaceRepository.findById(workspace.getId()).get().getName()).isEqualTo(request.name().get());
        assertThat(workspaceRepository.findById(workspace.getId()).get().getDescription()).isEqualTo(request.description().get());
    }

    @Test
    void workspaceMayNotBeUpdatedIfRequestIsInvalidAndEntityIsNotUpdated() throws Exception {

        // Arrange
        UUID ownerId = UUID.fromString("3adc4dd4-b5c0-4345-bf83-c44ef92188b9");

        Workspace workspace = Workspace.create(
                "name",
                "iconUrl",
                "description",
                ownerId
        );

        WorkspaceUpdateRequest request = new WorkspaceUpdateRequest(
                JsonNullable.of("""
                        verylongverylongverylongverylongverylongverylongverylongverylongverylongverylongv
                        erylongverylongverylongverylongverylongverylongverylongverylongverylongverylongve
                        rylongverylongverylongverylongverylongverylong
                        """),
                JsonNullable.undefined(),
                JsonNullable.of("new description")
        );

        workspace = workspaceRepository.save(workspace);

        // Act & Assert
        mockMvc.perform(patch("/api/v1/workspaces/%s".formatted(workspace.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("ValidationException: Provided argument violates validation constraints."))
                .andExpect(jsonPath("$.path").value("/api/v1/workspaces/%s".formatted(workspace.getId())))
                .andExpect(jsonPath("$.details").isNotEmpty());

        assertThat(workspaceRepository.count()).isEqualTo(1);
        assertThat(workspaceRepository.findById(workspace.getId())).isPresent();
        assertThat(workspaceRepository.findById(workspace.getId()).get().getName()).isEqualTo(workspace.getName());
        assertThat(workspaceRepository.findById(workspace.getId()).get().getDescription()).isEqualTo(workspace.getDescription());
    }

    @Test
    void workspaceMayNotBeUpdatedIfUserIsNotAnOwnerAndEntityIsNotUpdated() throws Exception {

        // Arrange
        UUID ownerId = UUID.randomUUID();

        Workspace workspace = Workspace.create(
                "name",
                "iconUrl",
                "description",
                ownerId
        );

        WorkspaceUpdateRequest request = new WorkspaceUpdateRequest(
                JsonNullable.of("new name"),
                JsonNullable.undefined(),
                JsonNullable.of("new description")
        );

        workspace = workspaceRepository.save(workspace);

        // Act & Assert
        mockMvc.perform(patch("/api/v1/workspaces/%s".formatted(workspace.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Workspace with id: %s not found.".formatted(workspace.getId())))
                .andExpect(jsonPath("$.path").value("/api/v1/workspaces/%s".formatted(workspace.getId())));

        assertThat(workspaceRepository.count()).isEqualTo(1);
        assertThat(workspaceRepository.findById(workspace.getId())).isPresent();
        assertThat(workspaceRepository.findById(workspace.getId()).get().getName()).isEqualTo(workspace.getName());
        assertThat(workspaceRepository.findById(workspace.getId()).get().getDescription()).isEqualTo(workspace.getDescription());
    }

    @Test
    void workspaceMayNotBeUpdatedIfArchived() throws Exception {

        // Arrange
        UUID ownerId = UUID.fromString("3adc4dd4-b5c0-4345-bf83-c44ef92188b9");

        Workspace workspace = Workspace.create(
                "name",
                "iconUrl",
                "description",
                ownerId
        );

        workspace.archive();

        workspace = workspaceRepository.save(workspace);

        WorkspaceUpdateRequest request = new WorkspaceUpdateRequest(
                JsonNullable.of("new name"),
                JsonNullable.undefined(),
                JsonNullable.of("new description")
        );

        // Act & Assert
        mockMvc.perform(patch("/api/v1/workspaces/%s".formatted(workspace.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Invariant violation: This action may not be performed due to resource's current state or an illegal input."))
                .andExpect(jsonPath("$.path").value("/api/v1/workspaces/%s".formatted(workspace.getId())));

        assertThat(workspaceRepository.count()).isEqualTo(1);
        assertThat(workspaceRepository.findById(workspace.getId())).isPresent();
        assertThat(workspaceRepository.findById(workspace.getId()).get().getName()).isEqualTo(workspace.getName());
        assertThat(workspaceRepository.findById(workspace.getId()).get().getDescription()).isEqualTo(workspace.getDescription());
    }

    @Test
    void nonExistentWorkspaceMayNotBeUpdatedAndOtherEntityIsNotUpdated() throws Exception {

        // Arrange
        UUID ownerId = UUID.fromString("3adc4dd4-b5c0-4345-bf83-c44ef92188b9");

        Workspace workspace = Workspace.create(
                "name",
                "iconUrl",
                "description",
                ownerId
        );

        WorkspaceUpdateRequest request = new WorkspaceUpdateRequest(
                JsonNullable.of("new name"),
                JsonNullable.undefined(),
                JsonNullable.of("new description")
        );

        workspace = workspaceRepository.save(workspace);

        UUID invalidWorkspaceId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(patch("/api/v1/workspaces/%s".formatted(invalidWorkspaceId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Workspace with id: %s not found.".formatted(invalidWorkspaceId)))
                .andExpect(jsonPath("$.path").value("/api/v1/workspaces/%s".formatted(invalidWorkspaceId)));

        assertThat(workspaceRepository.count()).isEqualTo(1);
        assertThat(workspaceRepository.findById(workspace.getId())).isPresent();
        assertThat(workspaceRepository.findById(workspace.getId()).get().getName()).isEqualTo(workspace.getName());
        assertThat(workspaceRepository.findById(workspace.getId()).get().getDescription()).isEqualTo(workspace.getDescription());
    }

    @Test
    void workspaceMayBeArchived() throws Exception {

        // Arrange
        UUID ownerId = UUID.fromString("3adc4dd4-b5c0-4345-bf83-c44ef92188b9");

        Workspace workspace = workspaceRepository.save(Workspace.create(
                "name",
                "iconUrl",
                "description",
                ownerId
        ));

        // Act & Assert
        mockMvc.perform(post("/api/v1/workspaces/%s/archive".formatted(workspace.getId()))
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.workspace_id").value(workspace.getId().toString()))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.icon_url").value("iconUrl"))
                .andExpect(jsonPath("$.state").value(WorkspaceState.ARCHIVED.toString()))
                .andExpect(jsonPath("$.owner_id").value(ownerId.toString()))
                .andExpect(jsonPath("$.icon_url").value("iconUrl"))
                .andExpect(jsonPath("$.description").value("description"));

        assertThat(workspaceRepository.count()).isEqualTo(1);
        assertThat(workspaceRepository.findById(workspace.getId())).isPresent();
        assertThat(workspaceRepository.findById(workspace.getId()).get().getState()).isEqualTo(WorkspaceState.ARCHIVED);
    }

    @Test
    void nonExistentWorkspaceMayNotBeArchived() throws Exception {

        // Arrange
        UUID ownerId = UUID.fromString("3adc4dd4-b5c0-4345-bf83-c44ef92188b9");

        Workspace workspace = workspaceRepository.save(Workspace.create(
                "name",
                "iconUrl",
                "description",
                ownerId
        ));

        UUID invalidWorkspaceId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(post("/api/v1/workspaces/%s/archive".formatted(invalidWorkspaceId))
                ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Workspace with id: %s not found.".formatted(invalidWorkspaceId)))
                .andExpect(jsonPath("$.path").value("/api/v1/workspaces/%s/archive".formatted(invalidWorkspaceId)));

        assertThat(workspaceRepository.count()).isEqualTo(1);
        assertThat(workspaceRepository.findById(workspace.getId())).isPresent();
        assertThat(workspaceRepository.findById(workspace.getId()).get().getState()).isEqualTo(WorkspaceState.ACTIVE);
    }
}


