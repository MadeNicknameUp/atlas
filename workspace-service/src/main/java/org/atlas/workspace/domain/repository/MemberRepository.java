package org.atlas.workspace.domain.repository;

import org.atlas.workspace.domain.model.Member;
import org.atlas.workspace.domain.model.MemberState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID>, JpaSpecificationExecutor<Member> {

    Optional<Member> findByIdAndWorkspaceIdAndWorkspaceOwnerId(UUID memberId, UUID workspaceId, UUID ownerId);

    Optional<Member> findByIdAndWorkspaceIdAndWorkspaceMembersUserIdAndState(UUID memberId, UUID workspaceId, UUID userId, MemberState state);
}
