package org.campusforum.backend.entity.vo.request;

import com.alibaba.fastjson2.JSONObject;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

/**
 * @author ChangxueDeng
 * @date 2024/04/09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTopicVO {
    @Min(1)
    private Integer type;
    @Length(min = 1, max = 30)
    private String title;
    private JSONObject content;
}
