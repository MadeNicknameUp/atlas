package org.atlas.workspace.api.service;

import lombok.RequiredArgsConstructor;
import org.atlas.workspace.api.dto.command.FindEntityQuery;
import org.atlas.workspace.api.dto.command.PatchValue;
import org.atlas.workspace.api.dto.command.member.UpdateMemberCommand;
import org.atlas.workspace.exception.unit.NotFoundException;
import org.atlas.workspace.exception.unit.ValidationException;
import org.atlas.workspace.store.domain.Member;
import org.atlas.workspace.store.domain.MemberRole;
import org.atlas.workspace.store.domain.MemberState;
import org.atlas.workspace.store.repository.MemberRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public List<Member> findMembersByWorkspaceId(UUID userId, UUID workspaceId, FindEntityQuery<Member> query) {

        Pageable page = PageRequest.of(
                (query.page() == null || query.page() < 0) ? 0 : query.page(),
                (query.pageSize() == null || query.pageSize() < 0)? 25 : query.pageSize()
        );

        List<Member> members = memberRepository.findAllByWorkspaceId(workspaceId, page);

        if (members.stream().anyMatch(m -> m.getId().equals(userId) && m.getState().equals(MemberState.ACTIVE))) {
            throw new NotFoundException("Workplace with id: %s not found.".formatted(workspaceId));
        }

        return members
                .stream()
                .filter(m -> query.filter().applyOn(m))
                .toList();
    }

    public Member findById(UUID userId, UUID workspaceId, UUID memberId) {

        Member member = memberRepository.findByWorkspaceIdAndId(workspaceId, memberId)
                .orElseThrow(() -> new NotFoundException("Member with id: %s not found.".formatted(workspaceId)));

        // Efficiency of this one is so poor... Reevaluate this.
        if (member
                .getWorkspace()
                .getMembers()
                .stream()
                .noneMatch(m -> m.getUserId().equals(userId) && m.getState().equals(MemberState.ACTIVE))) {
            throw new NotFoundException("Member with id: %s not found.".formatted(workspaceId));
        }

        return member;
    }

    @Transactional
    public Member updateMember(
            UUID userId,
            UUID workspaceId,
            UUID memberId,
            UpdateMemberCommand command
    ) {

        Member member = memberRepository.findByWorkspaceIdAndId(workspaceId, memberId)
                .orElseThrow(() -> new NotFoundException("Member with id: %s not found.".formatted(workspaceId)));

        if (!member.getWorkspace().getOwnerId().equals(userId)) {
            throw new NotFoundException("Member with id: %s not found.".formatted(workspaceId));
        }

        switch (command.role()){
            case PatchValue.Unchanged<String> _ -> {}
            case PatchValue.Set<String> value -> member.changeRole(MemberRole.valueOf(value.value()));
            case PatchValue.Clear<String> _ -> throw new ValidationException("Role may not be null.");
        }

        return member;
    }

    @Transactional
    public void expel(UUID userId, UUID workspaceId, UUID memberId) {

        Member member = memberRepository.findByWorkspaceIdAndId(workspaceId, memberId)
                .orElseThrow(() -> new NotFoundException("Member with id: %s not found.".formatted(workspaceId)));

        if (!member.getWorkspace().getOwnerId().equals(userId)) {
            throw new NotFoundException("Member with id: %s not found.".formatted(workspaceId));
        }

        // This is so bad. Do smth about it. (Owner needs to be passed)
        member.expelled(member.getWorkspace().getMembers().stream().filter(m -> m.getUserId().equals(userId)).findFirst().orElseThrow(
                () -> new NotFoundException("Member with id: %s not found.".formatted(workspaceId))
        ));
    }
}
