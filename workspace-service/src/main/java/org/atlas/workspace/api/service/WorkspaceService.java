package org.atlas.workspace.api.service;

import lombok.RequiredArgsConstructor;
import org.atlas.workspace.api.dto.command.CreateWorkspaceCommand;
import org.atlas.workspace.api.dto.command.FindWorkspacesQuery;
import org.atlas.workspace.api.dto.command.PatchValue;
import org.atlas.workspace.api.dto.command.WorkspaceUpdateCommand;
import org.atlas.workspace.exception.unit.NotFoundException;
import org.atlas.workspace.exception.unit.ValidationException;
import org.atlas.workspace.store.model.Member;
import org.atlas.workspace.store.model.Workspace;
import org.atlas.workspace.store.repository.MemberRepository;
import org.atlas.workspace.store.repository.WorkspaceRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public Workspace getWorkspaceById(UUID userId, UUID workspaceId) {

        Workspace workspace = workspaceRepository.findByIdWithMembers(workspaceId)
                .orElseThrow(() -> new NotFoundException("Workspace with id: %s not found.".formatted(workspaceId)));

        if (!workspace.getOwner().getUserId().equals(userId)) {
            throw new NotFoundException("Workspace with id: %s not found.".formatted(workspaceId));
        }

        return workspace;
    }

    public List<Workspace> getWorkspacesByUserId(UUID memberId, FindWorkspacesQuery query) {

        Pageable page = PageRequest.of(
                (query.page() == null || query.page() < 0) ? 0 : query.page(),
                (query.pageSize() == null || query.pageSize() <= 0)? 25 : query.pageSize()
        );

        return memberRepository
                .findAllByUserId(memberId, page)
                .stream()
                .map(Member::getWorkspace)
                .filter(w -> query.filter().applyOn(w))
                .toList();
    }

    @Transactional
    public Workspace updateWorkspace(UUID userId, UUID workspaceId, WorkspaceUpdateCommand workspaceUpdateCommand) {

        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("Workspace with id: %s not found.".formatted(workspaceId)));

        // This has to depend on workspace settings.
        if(!workspace.getOwner().getUserId().equals(userId)) {
            throw new NotFoundException("Workspace with id: %s not found.".formatted(workspaceId));
        }

        switch (workspaceUpdateCommand.name()){
            case PatchValue.Unchanged<String> _ -> {}
            case PatchValue.Set<String> value -> workspace.rename(value.value());
            case PatchValue.Clear<String> _ -> throw new ValidationException("Name may not be empty.");
        }

        switch (workspaceUpdateCommand.description()){
            case PatchValue.Unchanged<String> _ -> {}
            case PatchValue.Set<String> value -> workspace.modifyDescription(value.value());
            case PatchValue.Clear<String> _ -> workspace.clearDescription();
        }

        switch (workspaceUpdateCommand.iconUrl()){
            case PatchValue.Unchanged<String> _ -> {}
            case PatchValue.Set<String> value -> workspace.modifyIcon(value.value());
            case PatchValue.Clear<String> _ -> workspace.removeIcon();
        }


        return workspaceRepository.save(workspace);
    }

    @Transactional
    public Workspace archiveWorkspace(UUID userId, UUID workspaceId) {

        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("Workspace with id: %s not found.".formatted(workspaceId)));

        if(!workspace.getOwner().getUserId().equals(userId)) {
            throw new NotFoundException("Workspace with id: %s not found.".formatted(workspaceId));
        }

        workspace.archive();

        return workspaceRepository.save(workspace);
    }
}
