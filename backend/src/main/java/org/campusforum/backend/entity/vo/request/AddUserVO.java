package org.campusforum.backend.entity.vo.request;

import lombok.Data;

@Data
public class AddUserVO {
    private String username;
    private String password;
    private String email;
    private String role;
}
