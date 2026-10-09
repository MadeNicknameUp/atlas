package org.atlas.workspace.store.domain;

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
@Table(name = "atlas_workspace")
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
            mappedBy = "id",
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
        workspace.getMembers().add(Member.createOwner(
                ownerId,
                workspace
        ));
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
