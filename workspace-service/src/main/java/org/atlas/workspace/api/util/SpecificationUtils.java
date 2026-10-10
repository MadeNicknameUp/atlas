package org.atlas.workspace.api.util;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.atlas.workspace.store.model.Member;
import org.atlas.workspace.store.model.Workspace;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

public class SpecificationUtils {

    public static Specification<Workspace> containsUserId(UUID userId) {
        return (root, query, cb) -> {

            if (userId == null) {
                return null;
            }

            // Very defensive.
            query.distinct(true);

            Join<Workspace, Member> join = root.join("members", JoinType.INNER);

            return cb.equal(join.get("userId"), userId);
        };
    }

    public static Specification<Workspace> hasNameLike(String name) {

        return (root, query, cb) -> {

            if (name == null) {
                return null;
            }

            return cb.like(cb.lower(root.get("name")), name.toLowerCase(Locale.ROOT) + "%");
        };
    }

    public static Specification<Workspace> hasDescriptionLike(String description) {

        return (root, query, cb) -> {

            if (description == null) {
                return null;
            }

            return cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase(Locale.ROOT) + "%");
        };
    }

    public static Specification<Workspace> hasStateEqual(String state) {

        return (root, query, cb) -> {

            if (state == null) {
                return null;
            }

            return cb.equal(cb.lower(root.get("state")), state.toLowerCase(Locale.ROOT));
        };
    }

    public static Specification<Workspace> hasOwnerEqual(String ownerId) {

        return (root, query, cb) -> {

            if (ownerId == null) {
                return null;
            }

            return cb.equal(cb.lower(root.get("ownerId").get("userId")), ownerId.toLowerCase(Locale.ROOT));
        };
    }

    public static Specification<Workspace> isLaterThen(Instant date) {

        return (root, query, cb) -> {

            if (date == null) {
                return null;
            }

            return cb.greaterThanOrEqualTo(root.get("createdAt"), date);
        };
    }

    public static Specification<Workspace> isEarlierThen(Instant date) {

        return (root, query, cb) -> {

            if (date == null) {
                return null;
            }

            return cb.lessThanOrEqualTo(root.get("createdAt"), date);
        };
    }


}
