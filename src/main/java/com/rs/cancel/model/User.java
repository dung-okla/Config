package com.rs.cancel.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    @JsonIgnore
    private String password;
    private String email;

    @ManyToMany
    @JoinTable(name = "user_role",
    joinColumns = @JoinColumn(name = "user_name",referencedColumnName = "username"),
        inverseJoinColumns = @JoinColumn(name = "role_name",referencedColumnName = "role")
    )
    private Set<Role> role;

    @OneToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;
}
