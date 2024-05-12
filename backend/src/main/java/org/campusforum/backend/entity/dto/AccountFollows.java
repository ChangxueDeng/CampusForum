package org.campusforum.backend.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;


/**
 * 用户关注
 * @author ChangxueDeng
 * @date 2024/05/08
 */
@Data
@TableName("account_follows")
public class AccountFollows {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField
    private Integer follower;
    @TableField
    private Integer fans;
    @TableField
    private Date time;
}
