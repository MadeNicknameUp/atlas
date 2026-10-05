package org.atlas.workplaceservice.api.dto.request;

public record WorkspaceCreateRequest(
        String name,
        String description
) {
}
