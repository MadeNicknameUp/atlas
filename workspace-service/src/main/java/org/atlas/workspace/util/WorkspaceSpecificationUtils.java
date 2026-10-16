package org.atlas.workspace.util;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.atlas.workspace.domain.model.Member;
import org.atlas.workspace.domain.model.Workspace;
import org.atlas.workspace.domain.model.WorkspaceState;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

public class WorkspaceSpecificationUtils {

    public static Specification<Workspace> containsUserId(UUID userId) {
        return (root, query, cb) -> {

            // Very defensive.
            query.distinct(true);

            Join<Workspace, Member> join = root.join("members", JoinType.INNER);

            return cb.equal(join.get("userId"), userId);
        };
    }

    public static Specification<Workspace> hasNameLike(String name) {

        return (root, _, cb) -> {

            if (name == null || name.isBlank()) {
                return null;
            }

            return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase(Locale.ROOT) + "%");
        };
    }

    public static Specification<Workspace> hasDescriptionLike(String description) {

        return (root, _, cb) -> {

            if (description == null || description.isBlank()) {
                return null;
            }

            return cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase(Locale.ROOT) + "%");
        };
    }

    public static Specification<Workspace> hasStateEqual(String state) {

        return (root, _, cb) -> {

            if (state == null || state.isBlank()) {
                return null;
            }

            return cb.equal(root.get("state"), WorkspaceState.valueOf(state.toUpperCase(Locale.ROOT)));
        };
    }

    public static Specification<Workspace> hasOwnerEqual(String ownerId) {

        return (root, _, cb) -> {

            if (ownerId == null) {
                return null;
            }

            return cb.equal(root.get("ownerId"), UUID.fromString(ownerId));
        };
    }

    public static Specification<Workspace> isLaterThen(Instant date) {

        return (root, _, cb) -> {

            if (date == null) {
                return null;
            }

            return cb.greaterThanOrEqualTo(root.get("createdAt"), date);
        };
    }

    public static Specification<Workspace> isEarlierThen(Instant date) {

        return (root, _, cb) -> {

            if (date == null) {
                return null;
            }

            return cb.lessThanOrEqualTo(root.get("createdAt"), date);
        };
    }

}
