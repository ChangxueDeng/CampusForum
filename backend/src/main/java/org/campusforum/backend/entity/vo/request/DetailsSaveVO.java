package org.campusforum.backend.entity.vo.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

/**
 * 保存用户详细信息请求
 * @author ChangxueDeng
 * @date 2024/04/02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetailsSaveVO {
    String username;
    @Min(0)
    @Max(1)
    Integer gender;
    String phone;
    String qq;
    String wx;
    @Length(max = 200)
    String desc;
}
