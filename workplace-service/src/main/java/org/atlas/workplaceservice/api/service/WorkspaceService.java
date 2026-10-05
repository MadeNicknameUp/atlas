package org.atlas.workplaceservice.api.service;

import lombok.RequiredArgsConstructor;
import org.atlas.workplaceservice.api.dto.command.PatchValue;
import org.atlas.workplaceservice.api.dto.command.WorkspaceUpdateCommand;
import org.atlas.workplaceservice.exception.unit.NotFoundException;
import org.atlas.workplaceservice.exception.unit.ValidationException;
import org.atlas.workplaceservice.store.model.Member;
import org.atlas.workplaceservice.store.model.Workspace;
import org.atlas.workplaceservice.store.repository.MemberRepository;
import org.atlas.workplaceservice.store.repository.WorkspaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Workspace createWorkspace(String name, String description, UUID ownerId) {

        return workspaceRepository.save(Workspace.create(name, description, ownerId));
    }

    public Workspace getWorkplaceById(UUID workspaceId) {

        return workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("Workplace with id: %s not found.".formatted(workspaceId)));
    }

    public List<Workspace> getWorkplacesByUserId(UUID memberId) {

        return memberRepository
                .findAllByUserId(memberId)
                .stream()
                .map(Member::getWorkplace)
                .toList();
    }

    @Transactional
    public Workspace updateWorkspace(UUID workspaceId, WorkspaceUpdateCommand workspaceUpdateCommand) {

        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("Workplace with id: %s not found.".formatted(workspaceId)));

        switch (workspaceUpdateCommand.name()){
            case PatchValue.Unchanged<String> _ -> {}
            case PatchValue.Set<String> value -> workspace.rename(value.value());
            case PatchValue.Clear<String> _ -> throw new ValidationException("Name may not be empty.");
        }

        switch (workspaceUpdateCommand.description()){
            case PatchValue.Unchanged<String> _ -> {}
            case PatchValue.Set<String> value -> workspace.updateDescription(value.value());
            case PatchValue.Clear<String> _ -> workspace.clearDescription();
        }

        return workspaceRepository.save(workspace);
    }
}
