package org.atlas.workspace.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.atlas.workspace.api.dto.command.FindWorkspacesQuery;
import org.atlas.workspace.api.dto.request.WorkspaceCreateRequest;
import org.atlas.workspace.api.dto.request.WorkspaceFilter;
import org.atlas.workspace.api.dto.request.WorkspaceUpdateRequest;
import org.atlas.workspace.api.dto.response.ThinWorkspaceResponse;
import org.atlas.workspace.api.dto.response.WorkspaceCreatedResponse;
import org.atlas.workspace.api.dto.response.ThickWorkspaceResponse;
import org.atlas.workspace.api.service.WorkspaceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    @PostMapping
    public ResponseEntity<WorkspaceCreatedResponse> createWorkspace(
            @RequestBody @Valid WorkspaceCreateRequest request
    ) {

        UUID ownerId = UUID.fromString("3adc4dd4-b5c0-4345-bf83-c44ef92188b9");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(WorkspaceCreatedResponse.from(
                        workspaceService.createWorkspace(
                                ownerId,
                                request.toCommand()
                        )
        ));
    }

    @GetMapping
    public ResponseEntity<List<ThinWorkspaceResponse>> getWorkspaces(
            @ModelAttribute WorkspaceFilter filter,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false, name = "page_size") Integer pageSize
    ) {

        UUID userId = UUID.fromString("3adc4dd4-b5c0-4345-bf83-c44ef92188b9");

        return ResponseEntity.ok(workspaceService
                .getWorkspacesByUserId(userId, new FindWorkspacesQuery(filter, page, pageSize))
                .stream()
                .map(ThinWorkspaceResponse::from)
                .toList()
        );
    }

    // TODO: Should this actually return 'ThickWorkspaceResponse' or should members be fetched via member-oriented endpoint?
    @GetMapping("/{workspaceId}")
    public ResponseEntity<ThickWorkspaceResponse> getWorkspace(
            @PathVariable("workspaceId") UUID workspaceId
    ) {

        UUID userId = UUID.fromString("3adc4dd4-b5c0-4345-bf83-c44ef92188b9");

        return ResponseEntity.ok(ThickWorkspaceResponse.from(
                workspaceService.getWorkspaceById(userId, workspaceId)
        ));
    }

    @PatchMapping("/{workspaceId}")
    public ResponseEntity<ThinWorkspaceResponse> updateWorkspace(
            @PathVariable UUID workspaceId,
            @RequestBody WorkspaceUpdateRequest request
    ) {

        UUID userId = UUID.fromString("3adc4dd4-b5c0-4345-bf83-c44ef92188b9");

        return ResponseEntity.ok(ThinWorkspaceResponse.from(
                workspaceService.updateWorkspace(userId, workspaceId, request.toCommand())
        ));
    }

    @PostMapping("/{workspaceId}/archive")
    public ResponseEntity<ThinWorkspaceResponse> archiveWorkspace(@PathVariable UUID workspaceId) {

        UUID userId = UUID.fromString("3adc4dd4-b5c0-4345-bf83-c44ef92188b9");

        return ResponseEntity.ok(ThinWorkspaceResponse.from(
                workspaceService.archiveWorkspace(userId, workspaceId)
        ));
    }
}
