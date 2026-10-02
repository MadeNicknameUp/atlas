package org.atlas.workplaceservice.store.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
public class Workplace {

    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private UUID ownerId;

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    private List<Member> members = new ArrayList<>();

    @UpdateTimestamp
    private Instant updatedAt;

    @CreationTimestamp
    private Instant createdAt;

    public static Workplace create(String name, String description, UUID ownerId) {
        Workplace workplace = new Workplace();
        workplace.setName(name);
        workplace.setDescription(description);
        workplace.getMembers().add(new Member(ownerId, MemberRole.OWNER, workplace));
        workplace.ownerId = ownerId;
        return workplace;
    }
}
