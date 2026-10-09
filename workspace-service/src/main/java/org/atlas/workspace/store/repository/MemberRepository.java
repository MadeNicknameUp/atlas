package org.atlas.workspace.store.repository;

import org.atlas.workspace.store.domain.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {

    List<Member> findAllByUserId(UUID userId, Pageable pageable);

    @Query(value = "SELECT m from Member m WHERE m.workspace.id = :workspaceId")
    List<Member> findAllByWorkspaceId(UUID workspaceId, Pageable pageable);

    @Query(value = "SELECT m from Member m WHERE m.id = :memberId AND m.workspace.id = :workspaceId")
    Optional<Member> findByWorkspaceIdAndId(UUID workspaceId, UUID memberId);
}
