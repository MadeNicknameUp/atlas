package org.atlas.userservice.store.repository;

import org.atlas.userservice.store.model.User;
import org.hibernate.internal.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    @EntityGraph(attributePaths = "profile")
    Optional<User> findByIdentitySubject(String identitySubject);
}
