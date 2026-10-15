package org.atlas.workspace.domain.repository;

import org.atlas.workspace.domain.model.Workspace;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkspaceRepository
        extends JpaRepository<Workspace, UUID>, JpaSpecificationExecutor<Workspace> {

    @EntityGraph(attributePaths = "members")
    Optional<Workspace> findByIdAndMembersUserId(UUID workspaceId, UUID userId);

    Optional<Workspace> findByIdAndOwnerId(UUID workspaceId, UUID ownerId);

}
