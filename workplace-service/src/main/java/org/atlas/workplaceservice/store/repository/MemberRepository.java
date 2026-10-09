package org.atlas.workplaceservice.store.repository;

import org.atlas.workplaceservice.store.model.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {

    List<Member> findAllByUserId(UUID userId, Pageable pageable);
}
