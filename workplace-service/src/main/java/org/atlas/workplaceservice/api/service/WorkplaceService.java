package org.atlas.workplaceservice.api.service;

import lombok.RequiredArgsConstructor;
import org.atlas.workplaceservice.store.model.Workplace;
import org.atlas.workplaceservice.store.repository.WorkplaceRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkplaceService {

    private final WorkplaceRepository workplaceRepository;

    public Workplace createWorkplace(String name, String description, UUID ownerId) {

        return workplaceRepository.save(Workplace.create(name, description, ownerId));
    }
}
