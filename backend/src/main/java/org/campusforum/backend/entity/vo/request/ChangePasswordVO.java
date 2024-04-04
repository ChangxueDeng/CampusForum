package org.campusforum.backend.entity.vo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

/**
 * 修改密码请求
 * @author ChangxueDeng
 * @date 2024/04/04
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordVO {
    @Length(min = 6, max = 16)
    private String password;
    @Length(min = 6, max = 16)
    private String newPassword;
}
