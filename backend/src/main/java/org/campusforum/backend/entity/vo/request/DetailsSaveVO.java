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
    @Length(min = 2, max = 10)
    private String username;
    @Min(0)
    @Max(1)
    private Integer gender;
    @Length(min = 11, max = 11)
    private String phone;
    @Length(max = 13)
    private String qq;
    @Length(max = 20)
    private String wx;
    @Length(max = 200)
    private String desc;
}
