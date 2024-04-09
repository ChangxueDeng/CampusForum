package org.campusforum.backend.entity.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * 论坛帖子图片
 * @author ChangxueDeng
 */
@Data
@AllArgsConstructor
@TableName(value = "image_store")
public class ImageStore {
    private Integer uid;
    private String name;
    private Date time;
}
