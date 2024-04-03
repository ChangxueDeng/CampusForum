package org.campusforum.backend.entity.dto;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Account类对应account表，记录账户信息，用于登陆验证等
 * @author ChangxueDeng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "account")
public class Account {

    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField
    private String username;
    @TableField
    private String password;
    @TableField
    private String email;
    @TableField
    private String role;
    @TableField
    private Date registerTime;
}
