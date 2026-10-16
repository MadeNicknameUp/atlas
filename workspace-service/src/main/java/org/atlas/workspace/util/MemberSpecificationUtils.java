package org.atlas.workspace.util;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.atlas.workspace.domain.model.Member;
import org.atlas.workspace.domain.model.MemberRole;
import org.atlas.workspace.domain.model.MemberState;
import org.atlas.workspace.domain.model.Workspace;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

public class MemberSpecificationUtils {

//    List<Member> members = memberRepository.findAllByWorkspaceIdAndWorkspaceMembersUserIdAndState(
//            workspaceId,
//            userId,
//            MemberState.ACTIVE,
//            specification,
//            page
//    );

    public static Specification<Member> ifActorIsMember(UUID actorId) {

        return (root, query, cb) -> {
            Subquery<Integer> subquery = query.subquery(Integer.class);

            Root<Member> member = subquery.from(Member.class);

            subquery.select(cb.literal(1))
                    .where(
                            cb.equal(member.get("workspace"), root.get("workspace")), // member.wprkspace == actor.workspace
                            cb.equal(member.get("userId"), actorId), // member.userId == actorId
                            cb.equal(member.get("state"), MemberState.ACTIVE) // member.state == MemberState.ACTIVE
                    );

            return cb.exists(subquery);
        };
    }

    public static Specification<Member> allMembersByWorkspace(UUID workspaceId) {

        return (root, _, cb) -> {

            Join<Member, Workspace> join = root.join("workspace", JoinType.INNER);

            return cb.equal(join.get("id"), workspaceId);
        };
    }

    public static Specification<Member> hasRoleEqual(String role) {

        return (root, _, cb) -> {

            if (role == null || role.isBlank()) {
                return null;
            }

            return cb.equal(root.get("role"), MemberRole.valueOf(role.toUpperCase(Locale.ROOT)));
        };
    }

    public static Specification<Member> hasStateEqual(String state) {

        return (root, _, cb) -> {

            if (state == null || state.isBlank()) {
                return null;
            }

            return cb.equal(root.get("state"), MemberState.valueOf(state.toUpperCase(Locale.ROOT)));
        };
    }

    public static Specification<Member> wasInvitedBy(String memberId) {

        return (root, _, cb) -> {

            if (memberId == null || memberId.isBlank()) {
                return null;
            }

            return cb.equal(root.get("invitedBy"), UUID.fromString(memberId));
        };
    }

    public static Specification<Member> wasExpelledBy(String memberId) {

        return (root, _, cb) -> {

            if (memberId == null || memberId.isBlank()) {
                return null;
            }

            return cb.equal(root.get("expelledBy"), UUID.fromString(memberId));
        };
    }

    public static Specification<Member> isLaterThen(Instant date) {

        return (root, _, cb) -> {

            if (date == null) {
                return null;
            }

            return cb.greaterThanOrEqualTo(root.get("joinedAt"), date);
        };
    }

    public static Specification<Member> isEarlierThen(Instant date) {

        return (root, _, cb) -> {

            if (date == null) {
                return null;
            }

            return cb.lessThanOrEqualTo(root.get("joinedAt"), date);
        };
    }

}
