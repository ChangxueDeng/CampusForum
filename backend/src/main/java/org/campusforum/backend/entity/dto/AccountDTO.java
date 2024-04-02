package org.campusforum.backend.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {
    private int id;
    private String username;
    private String role;
    private String email;
    private Date registerTime;
}