package org.atlas.workplaceservice.store.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "atlas_workplaces")
@NoArgsConstructor
@AllArgsConstructor
public class Workspace {

    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String iconUrl;

    private String description;

    private WorkspaceState state;

    @Column(nullable = false)
    private UUID ownerId;

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<Member> members = new ArrayList<>();

    @UpdateTimestamp
    private Instant updatedAt;

    @CreationTimestamp
    private Instant createdAt;

    public static Workspace create(String name, String iconUrl, String description, UUID ownerId) {

        Workspace workspace = new Workspace();

        workspace.setName(name);
        workspace.setIconUrl(iconUrl);
        workspace.setDescription(description);
        workspace.getMembers().add(new Member(ownerId, MemberRole.OWNER, workspace));
        workspace.ownerId = ownerId;
        workspace.state = WorkspaceState.ACTIVE;

        return workspace;
    }

    public void rename(@NonNull String newName) {
        this.name = newName;
    }

    public void updateDescription(String newDescription) {
        this.description = newDescription;
    }

    public void clearDescription() {
        description = "";
    }

    public void archive() {

        if (state == WorkspaceState.ARCHIVED) {
            throw new IllegalStateException("Workspace with id: %s is already archived.".formatted(this.id));
        }

        state = WorkspaceState.ARCHIVED;
    }
}
