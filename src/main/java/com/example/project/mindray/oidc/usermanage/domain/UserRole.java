package com.example.project.mindray.oidc.usermanage.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private int id;

    @Column(nullable = false, unique = true)
    private String rolename;


    @Column(name = "author_id", nullable = false)
    private int authorId;

    public UserRole(String name,int authorId) {
        this.rolename = name;
        this.authorId = authorId;
    }

}
