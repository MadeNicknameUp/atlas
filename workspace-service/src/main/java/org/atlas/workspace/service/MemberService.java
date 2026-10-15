package org.atlas.workspace.service;

import lombok.RequiredArgsConstructor;
import org.atlas.workspace.api.dto.command.FindDomainQuery;
import org.atlas.workspace.api.dto.command.PatchValue;
import org.atlas.workspace.api.dto.command.MemberUpdateCommand;
import org.atlas.workspace.api.dto.request.MemberFilter;
import org.atlas.workspace.exception.unit.NotFoundException;
import org.atlas.workspace.exception.unit.ValidationException;
import org.atlas.workspace.domain.model.Member;
import org.atlas.workspace.domain.model.MemberRole;
import org.atlas.workspace.domain.model.MemberState;
import org.atlas.workspace.domain.repository.MemberRepository;
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

    // TODO: This method is trash and needs to be reworked.
    public List<Member> findMembersByWorkspaceId(UUID userId, UUID workspaceId, FindDomainQuery<MemberFilter> query) {

        Pageable page = PageRequest.of(
                (query.page() == null || query.page() < 0) ? 0 : query.page(),
                (query.pageSize() == null || query.pageSize() < 0)? 25 : query.pageSize()
        );

        // Where are the filters?

        List<Member> members = memberRepository.findAllByWorkspaceId(workspaceId, page);

        // Wth is this, this does not even work. You look ONLY on the page, not an entire list, just delegate to DB.
        if (members.stream().anyMatch(m -> m.getId().equals(userId) && m.getState().equals(MemberState.ACTIVE))) {
            throw new NotFoundException("Workplace with id: %s not found.".formatted(workspaceId));
        }

        return members;
//                .stream()
//                .filter(m -> query.filter().applyOn(m))
//                .toList();
    }

    public Member findById(UUID userId, UUID workspaceId, UUID memberId) {

        // Why is Owner the only one to see this? This is wrong, another method is needed.
        Member member = memberRepository.findByIdAndWorkspaceIdAndWorkspaceOwnerId(memberId, workspaceId, userId)
                .orElseThrow(() -> new NotFoundException("Member with id: %s not found.".formatted(workspaceId)));

        // Just delegate this to DB (in one query plz).
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
            MemberUpdateCommand command
    ) {

        Member member = memberRepository.findByIdAndWorkspaceIdAndWorkspaceOwnerId(memberId, workspaceId, userId)
                .orElseThrow(() -> new NotFoundException("Member with id: %s not found.".formatted(workspaceId)));

        switch (command.role()){
            case PatchValue.Unchanged<String> _ -> {}
            case PatchValue.Set<String> value -> member.changeRole(MemberRole.valueOf(value.value()));
            case PatchValue.Clear<String> _ -> throw new ValidationException("Role may not be null.");
        }

        return member;
    }

    @Transactional
    public void expel(UUID userId, UUID workspaceId, UUID memberId) {

        Member member = memberRepository.findByIdAndWorkspaceIdAndWorkspaceOwnerId(memberId, workspaceId, userId)
                .orElseThrow(() -> new NotFoundException("Member with id: %s not found.".formatted(workspaceId)));

        member.expel(memberRepository.findAllByWorkspaceIdAndId(workspaceId, memberId).orElseThrow(
                () -> new NotFoundException("Member with id: %s not found.".formatted(workspaceId))
        ));
    }
}

