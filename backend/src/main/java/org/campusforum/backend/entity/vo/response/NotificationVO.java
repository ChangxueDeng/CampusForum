package org.campusforum.backend.entity.vo.response;


import lombok.Data;

import java.util.Date;

@Data
public class NotificationVO {
    private Integer id;
    private String title;
    private String content;
    private String type;
    private String url;
    private Date time;
}
