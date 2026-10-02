package org.atlas.workplaceservice.api.controller;

import lombok.RequiredArgsConstructor;
import org.atlas.workplaceservice.api.dto.request.WorkplaceCreatedRequest;
import org.atlas.workplaceservice.api.dto.response.WorkplaceCreatedResponse;
import org.atlas.workplaceservice.api.service.WorkplaceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workplaces")
@RequiredArgsConstructor
public class WorkplaceController {

    private final WorkplaceService workplaceService;

    @PostMapping("/{ownerId}")
    public ResponseEntity<WorkplaceCreatedResponse> createWorkplace(
            @RequestBody WorkplaceCreatedRequest request,
            @PathVariable(name= "ownerId") UUID ownerId
            ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(WorkplaceCreatedResponse.from(
                        workplaceService.createWorkplace(
                                request.name(),
                                request.description(),
                                ownerId)
        ));
    }
}
