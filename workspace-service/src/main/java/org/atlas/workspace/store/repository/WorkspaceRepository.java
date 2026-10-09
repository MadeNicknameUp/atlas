package org.atlas.workspace.store.repository;

import org.atlas.workspace.store.domain.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, UUID> {

    // N + 1 with Members over here. Solve it.
    // I suggest 2 different queries, one to fetch without members, second one to fetch with members

}
