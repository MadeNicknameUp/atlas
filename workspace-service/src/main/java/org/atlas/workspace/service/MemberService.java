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
import org.atlas.workspace.util.MemberSpecificationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public List<Member> findMembersByWorkspaceId(UUID userId, UUID workspaceId, FindDomainQuery<MemberFilter> query) {

        Pageable page = PageRequest.of(
                (query.page() == null || query.page() < 0) ? 0 : query.page(),
                (query.pageSize() == null || query.pageSize() < 0)? 25 : query.pageSize(),
                Sort.by(
                        Sort.Order.desc("joinedAt"),
                        Sort.Order.desc("id")
                )
        );

        Specification<Member> specification = MemberSpecificationUtils.allMembersByWorkspace(workspaceId);

        specification = specification.and(MemberSpecificationUtils.ifActorIsMember(userId));
        specification = specification.and(MemberSpecificationUtils.hasRoleEqual(query.filter().role()));
        specification = specification.and(MemberSpecificationUtils.hasStateEqual(query.filter().state()));
        specification = specification.and(MemberSpecificationUtils.isLaterThen(query.filter().joinedFrom()));
        specification = specification.and(MemberSpecificationUtils.isEarlierThen(query.filter().joinedUntil()));
        specification = specification.and(MemberSpecificationUtils.wasExpelledBy(query.filter().expelledBy()));
        specification = specification.and(MemberSpecificationUtils.wasInvitedBy(query.filter().invitedBy()));

        Page<Member> members = memberRepository.findAll(
                specification,
                page
                );

        if (members.isEmpty()) {
            throw new NotFoundException("No members for workspace: %s found.".formatted(workspaceId));
        }

        return members.getContent();
    }

    public Member findMemberById(UUID userId, UUID workspaceId, UUID memberId) {

        return memberRepository.findByIdAndWorkspaceIdAndWorkspaceMembersUserIdAndState(
                    memberId,
                    workspaceId,
                    userId,
                    MemberState.ACTIVE
                ).orElseThrow(() -> new NotFoundException("Member with id: %s not found.".formatted(workspaceId)));
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

        member.expel(member);
    }
}

