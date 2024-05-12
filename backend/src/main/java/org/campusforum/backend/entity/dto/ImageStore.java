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
 * 论坛帖子图片
 * @author ChangxueDeng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "image_store")
public class ImageStore {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField(value = "uid")
    private Integer uid;
    @TableField(value = "name")
    private String name;
    @TableField(value = "time")
    private Date time;
}
