package org.atlas.userservice.store.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "atlas_profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(length = 255)
    private String fullName;

    @Column(length = 100)
    private String displayName;

    @Column(length = 50)
    private String username;

    @Column(length = 1000)
    private String about;

    @Column
    private String position;

    public Profile(User user) {
        this.user = user;
    }

}
