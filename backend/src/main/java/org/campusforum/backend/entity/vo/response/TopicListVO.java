package org.campusforum.backend.entity.vo.response;

import lombok.Data;


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
    private String contentAbstract;
}
