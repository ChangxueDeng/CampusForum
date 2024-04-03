package org.campusforum.backend.entity.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 账户信息响应
 * @author ChangxueDeng
 * @date 2024/04/02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountVO {
    private String username;
    private String role;
    private String email;
    private Date registerTime;
}
