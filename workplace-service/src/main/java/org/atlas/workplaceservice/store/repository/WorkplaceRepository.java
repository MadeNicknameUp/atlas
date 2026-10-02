package org.atlas.workplaceservice.store.repository;

import org.atlas.workplaceservice.store.model.Workplace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WorkplaceRepository extends JpaRepository<Workplace, UUID> {
}
