package org.atlas.workspace.domain.repository;

import org.atlas.workspace.domain.model.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {

    List<Member> findAllByWorkspaceId(UUID workspaceId, Pageable pageable);

    Optional<Member> findAllByWorkspaceIdAndId(UUID workspaceId, UUID memberId);

//    @Query(value = "SELECT m from Member m WHERE m.id = :memberId AND m.workspace.id = :workspaceId")
    Optional<Member> findByIdAndWorkspaceIdAndWorkspaceOwnerId(UUID memberId, UUID workspaceId, UUID ownerId);
}
