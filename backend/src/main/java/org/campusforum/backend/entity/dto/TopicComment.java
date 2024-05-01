package org.campusforum.backend.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("topic_comment")
public class TopicComment {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField
    private Integer tid;
    @TableField
    private Integer uid;
    @TableField
    private String content;
    @TableField
    private Date time;
    @TableField
    private Integer quote;

}
