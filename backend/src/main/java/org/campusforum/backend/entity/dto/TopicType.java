package org.campusforum.backend.entity.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author ChangxueDeng
 */
@Data
@AllArgsConstructor
@TableName("topic_type")
public class TopicType {
    @TableId()
    private Integer id;
    @TableField
    private String name;
    @TableField("`desc`")
    private String desc;
    @TableField
    private String color;
}