package org.campusforum.backend.entity.dto;

import lombok.Data;

@Data
public class AccountDTO {
    private int id;
    private String username;
    private String role;
    private String email;
}
