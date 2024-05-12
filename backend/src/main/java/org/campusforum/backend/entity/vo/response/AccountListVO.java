package org.campusforum.backend.entity.vo.response;

import lombok.Data;

import java.util.Date;


/**
 * @author ChangxueDeng
 * @date 2024/05/04
 */
@Data
public class AccountListVO {
    private Integer id;
    private String username;
    private String role;
    private String email;
    private Date registerTime;
    private String avatar;
    private Integer gender;
    private String phone;
    private String qq;
    private String wx;
    private String desc;
    private Boolean ban;
}
