package org.atlas.workspace.store.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "atlas_membership")
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberState state;

    @ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH },
            fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;

    @CreationTimestamp
    private Instant joinedAt;

    @OneToOne(cascade = { CascadeType.DETACH , CascadeType.MERGE, CascadeType.REFRESH },
            fetch = FetchType.LAZY)
    private Member invitedBy;

    @UpdateTimestamp
    private Instant updatedAt;

    // Does this one make sense? Should that not be a part of activity?
    @OneToOne(cascade = { CascadeType.DETACH , CascadeType.MERGE, CascadeType.REFRESH },
            fetch = FetchType.LAZY)
    private Member updatedBy;

    private Instant expelledAt;

    @OneToOne(cascade = { CascadeType.DETACH , CascadeType.MERGE, CascadeType.REFRESH },
            fetch = FetchType.LAZY)
    private Member expelledBy;

    public static Member createOwner(UUID ownerId, Workspace workspace) {

        Member member = new Member()
                .setId(null)
                .setUserId(ownerId)
                .setRole(MemberRole.OWNER)
                .setState(MemberState.ACTIVE)
                .setWorkspace(workspace)
                .setJoinedAt(null)
                .setExpelledAt(null)
                .setUpdatedAt(null)
                .setExpelledBy(null);

        member.setUpdatedBy(member);
        member.setInvitedBy(member);

        return member;
    }

    public void expel(Member actor) {

        if (role == MemberRole.OWNER) {
            throw new IllegalStateException("Owner may not be expelled.");
        }

        if (state == MemberState.EXPELLED) {
            throw new IllegalStateException("Member is already expelled.");
        }

        state = MemberState.EXPELLED;
        expelledAt = Instant.now();
        expelledBy = actor;
    }

    public void changeRole(MemberRole newRole) {

        // Cannot be changed to OWNER and same role, ig

        role = newRole;
    }
}
