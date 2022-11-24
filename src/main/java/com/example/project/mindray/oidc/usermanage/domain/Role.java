package com.example.project.mindray.oidc.usermanage.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Role  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private int id;



    @Column(nullable = false, unique = true)
    private String name;


    @Column
    private String descri;

    @Column(nullable = false, unique = true)
    private String isdefault;

    public Role(String name) {
        this.name = name;
    }

}
