package org.atlas.workplaceservice.api.service;

import lombok.RequiredArgsConstructor;
import org.atlas.workplaceservice.exception.unit.NotFoundException;
import org.atlas.workplaceservice.store.model.Workplace;
import org.atlas.workplaceservice.store.repository.WorkplaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkplaceService {

    private final WorkplaceRepository workplaceRepository;

    @Transactional
    public Workplace createWorkplace(String name, String description, UUID ownerId) {

        return workplaceRepository.save(Workplace.create(name, description, ownerId));
    }

    public Workplace getWorkplaceById(UUID id) {

        return workplaceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Workplace with id: %s not found.".formatted(id)));
    }
}
