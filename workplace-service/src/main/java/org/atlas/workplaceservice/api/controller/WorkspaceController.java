package org.atlas.workplaceservice.api.controller;

import lombok.RequiredArgsConstructor;
import org.atlas.workplaceservice.api.dto.request.WorkspaceCreateRequest;
import org.atlas.workplaceservice.api.dto.request.WorkspaceUpdateRequest;
import org.atlas.workplaceservice.api.dto.response.ThinWorkspaceResponse;
import org.atlas.workplaceservice.api.dto.response.WorkspaceCreatedResponse;
import org.atlas.workplaceservice.api.dto.response.ThickWorkspaceResponse;
import org.atlas.workplaceservice.api.service.WorkspaceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workplaces")
@RequiredArgsConstructor
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    @PostMapping
    public ResponseEntity<WorkspaceCreatedResponse> createWorkplace(
            @RequestBody WorkspaceCreateRequest request,
            @PathVariable(name= "ownerId") UUID ownerId
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(WorkspaceCreatedResponse.from(
                        workspaceService.createWorkspace(
                                request.name(),
                                request.description(),
                                ownerId)
        ));
    }

    @GetMapping
    public ResponseEntity<List<ThickWorkspaceResponse>> getWorkplaces(
            @PathVariable("workspaceId") UUID workspaceId
    ) {

        return ResponseEntity.ok(workspaceService
                .getWorkplacesByUserId(workspaceId)
                .stream()
                .map(ThickWorkspaceResponse::from)
                .toList()
        );
    }

    @GetMapping("/{workspaceId}")
    public ResponseEntity<ThinWorkspaceResponse> getWorkplace(
            @PathVariable("workspaceId") UUID workspaceId
    ) {

        return ResponseEntity.ok(ThinWorkspaceResponse.from(
                workspaceService.getWorkplaceById(workspaceId)
        ));
    }

    @PatchMapping("/{workspaceId}")
    public ResponseEntity<ThickWorkspaceResponse> updateWorkspace(
            @PathVariable UUID workspaceId,
            @RequestBody WorkspaceUpdateRequest request
    ) {

        return ResponseEntity.ok(ThickWorkspaceResponse.from(
                workspaceService.updateWorkspace(workspaceId, request.toCommand())
        ));
    }
}
