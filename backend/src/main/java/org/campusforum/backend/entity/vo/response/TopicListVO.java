package org.campusforum.backend.entity.vo.response;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import org.campusforum.backend.entity.dto.Topic;

import java.util.Date;

@Data
public class TopicListVO {
    private Integer id;
    private String title;
    private String content;
    private Integer type;
    private Date time;
    private Integer uid;
    private Boolean ban;
    private Boolean top;
}
