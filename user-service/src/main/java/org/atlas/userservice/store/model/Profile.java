package org.atlas.userservice.store.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    // OneToOne with User
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(name = "display_name", length = 100)
    private String displayName;

    @Column(name = "username", length = 100)
    private String userName;

    @Column(name = "about", length = 300)
    private String about;

    @Column(name = "position")
    private String position;

}
