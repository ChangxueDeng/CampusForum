package org.campusforum.backend.entity.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户隐私设置
 * @author ChangxueDeng
 * @date 2024/04/05
 */
@Data
@TableName("account_privacy")
public class AccountPrivacy {
    @TableId()
    private final Integer id;
    @TableField
    private boolean gender = true;
    @TableField
    private boolean phone = true;
    @TableField
    private boolean qq = true;
    @TableField
    private boolean wx = true;
    @TableField
    private boolean email = true;
}
