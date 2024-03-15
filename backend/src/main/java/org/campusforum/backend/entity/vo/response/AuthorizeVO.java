package org.campusforum.backend.entity.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 登陆成功响应
 * @author ChangxueDeng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorizeVO {
    private String username;
    private String role;
    private String token;
    private Date expire;
}
