package org.campusforum.backend.entity.vo.request;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

/**
 * 重置密码请求
 * @author ChangxueDeng
 * @date 2024/04/02
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResetPasswordVO {
    @Email
    private String email;
    private String password;
    @Length(min = 6, max = 6)
    private String code;
}
