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
 * @author ChangxueDeng
 * @date 2024/04/09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("topic")
public class Topic {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField
    private String title;
    @TableField
    private String content;
    @TableField
    private Integer type;
    @TableField
    private Date time;
    @TableField
    private Integer uid;
    @TableField
    private boolean ban;
    @TableField
    private boolean top;
}
