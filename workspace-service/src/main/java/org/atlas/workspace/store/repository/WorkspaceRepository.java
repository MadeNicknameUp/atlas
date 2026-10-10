package org.atlas.workspace.store.repository;

import org.atlas.workspace.store.model.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkspaceRepository
        extends JpaRepository<Workspace, UUID>, JpaSpecificationExecutor<Workspace> {

    @Query("SELECT w FROM Workspace w JOIN FETCH w.members WHERE w.id = :workspaceId")
    Optional<Workspace> findByIdWithMembers(UUID workspaceId);

}
