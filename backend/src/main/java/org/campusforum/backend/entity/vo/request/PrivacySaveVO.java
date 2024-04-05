package org.campusforum.backend.entity.vo.request;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 隐私设置保存
 * @author ChangxueDeng
 * @date 2024/04/05
 */
@Data
public class PrivacySaveVO {
    @Pattern(regexp = "(gender|phone|qq|wx|email)")
    private String type;
    private Boolean status;
}
