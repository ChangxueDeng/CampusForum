package org.campusforum.backend.entity.vo.response;

import lombok.Data;

import java.util.Date;

@Data
public class AnnouncementVO {
    private String title;
    private String content;
    private Date time;
}
