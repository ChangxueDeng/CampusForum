package org.campusforum.backend.entity.vo.response;

import lombok.Data;

import java.util.Date;

/**
 * 登陆成功响应
 * @author ChangxueDeng
 */
@Data
public class AuthorizeVO {
    private String username;
    private String role;
    private String token;
    private Date expire;
}
