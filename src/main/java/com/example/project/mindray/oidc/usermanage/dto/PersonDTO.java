package com.example.project.mindray.oidc.usermanage.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PersonDTO {
    private String name;
    private String password;
    private LocalDateTime created;
    private int roleId;
}
