package org.atlas.workplaceservice.api.service;

import lombok.RequiredArgsConstructor;
import org.atlas.workplaceservice.api.dto.command.CreateWorkspaceCommand;
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
    public Workspace createWorkspace(UUID ownerId, CreateWorkspaceCommand createWorkspaceCommand) {

        return workspaceRepository.save(Workspace.create(
                createWorkspaceCommand.name(),
                createWorkspaceCommand.iconUrl(),
                createWorkspaceCommand.description(),
                ownerId
                ));
    }

    public Workspace getWorkplaceById(UUID userId, UUID workspaceId) {

        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("Workplace with id: %s not found.".formatted(workspaceId)));

        // This has to be replaced with smth more efficient later.
        if (workspace.getMembers().stream().noneMatch(member -> member.getUserId().equals(userId))) {
            throw new NotFoundException("Workplace with id: %s not found.".formatted(workspaceId));
        }

        return workspace;
    }

    public List<Workspace> getWorkplacesByUserId(UUID memberId) {

        return memberRepository
                .findAllByUserId(memberId)
                .stream()
                .map(Member::getWorkplace)
                .toList();
    }

    @Transactional
    public Workspace updateWorkspace(UUID userId, UUID workspaceId, WorkspaceUpdateCommand workspaceUpdateCommand) {

        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("Workplace with id: %s not found.".formatted(workspaceId)));

        // This has to be A: Changed later. B: Depend on workspace settings.
        if(!workspace.getOwnerId().equals(userId)) {
            throw new NotFoundException("Workplace with id: %s not found.".formatted(workspaceId));
        }

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

    public Workspace archiveWorkspace(UUID userId, UUID workspaceId) {

        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("Workplace with id: %s not found.".formatted(workspaceId)));

        if(!workspace.getOwnerId().equals(userId)) {
            throw new NotFoundException("Workplace with id: %s not found.".formatted(workspaceId));
        }

        workspace.archive();

        return workspace;
    }
}
