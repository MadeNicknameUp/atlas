package org.atlas.workspace.store.repository;

import org.atlas.workspace.store.model.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkspaceRepository
        extends JpaRepository<Workspace, UUID>, JpaSpecificationExecutor<Workspace> {

    Optional<Workspace> findByIdAndMembersUserId(UUID workspaceId, UUID userId);
}
