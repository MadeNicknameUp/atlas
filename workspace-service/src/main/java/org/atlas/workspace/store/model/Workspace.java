package org.atlas.workspace.store.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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

    @Column(columnDefinition = "TEXT")
    private String iconUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private WorkspaceState state;

    @OneToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "owner_id")
    private Member owner;

    @OneToMany(
            mappedBy = "workspace",
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
        workspace.owner = new Member(ownerId, MemberRole.OWNER, workspace);
        workspace.getMembers().add(workspace.owner);
        workspace.state = WorkspaceState.ACTIVE;

        return workspace;
    }

    public void rename(@NotBlank String newName) {

        if (newName.isBlank()) {
            throw new IllegalArgumentException("Name may not be empty.");
        } else if (state == WorkspaceState.ARCHIVED) {
            throw new IllegalStateException("Cannot modify archived workspace.");
        }

        this.name = newName;
    }

    public void modifyDescription(String newDescription) {

        if (state == WorkspaceState.ARCHIVED) {
            throw new IllegalStateException("Cannot modify archived workspace.");
        }

        this.description = newDescription;
    }

    public void clearDescription() {

        if (state == WorkspaceState.ARCHIVED) {
            throw new IllegalStateException("Workspace with id: %s is already archived.".formatted(this.id));
        }

        description = "";
    }

    public void archive() {

        if (state == WorkspaceState.ARCHIVED) {
            throw new IllegalStateException("Workspace with id: %s is already archived.".formatted(this.id));
        }

        state = WorkspaceState.ARCHIVED;
    }

    public void modifyIcon(String value) {

        if (state == WorkspaceState.ARCHIVED) {
            throw new IllegalStateException("Cannot modify archived workspace.");
        }

        this.iconUrl = value;
    }

    public void removeIcon() {

        if (state == WorkspaceState.ARCHIVED) {
            throw new IllegalStateException("Cannot modify archived workspace.");
        }

        this.iconUrl = null;
    }
}
