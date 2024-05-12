package org.campusforum.backend.entity.vo.response;

import lombok.Data;

import java.util.Date;
@Data
public class AnnouncementAdminVO {
    private Integer id;
    private Integer uid;
    private String title;
    private String content;
    private Date time;
}
