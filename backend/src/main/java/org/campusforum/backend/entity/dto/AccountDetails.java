package org.campusforum.backend.entity.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 用户详细信息类对应 account_details表
 * @author ChangxueDeng
 * @date 2024/04/02
 */
@Data
@TableName(value = "account_details")
@AllArgsConstructor
@NoArgsConstructor
public class AccountDetails {
    @TableId
    Integer id;
    @TableField
    int gender;
    @TableField
    String phone;
    @TableField
    String qq;
    @TableField
    String wx;
    @TableField
    String desc;
}
