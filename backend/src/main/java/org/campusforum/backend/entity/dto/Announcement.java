package org.campusforum.backend.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("announcement")
public class Announcement {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField
    private Integer uid;
    @TableField
    private String title;
    @TableField
    private String content;
    @TableField
    private Date time;
}
