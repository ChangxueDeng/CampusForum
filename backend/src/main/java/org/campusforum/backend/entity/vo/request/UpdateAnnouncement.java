package org.campusforum.backend.entity.vo.request;

import lombok.Data;

@Data
public class UpdateAnnouncement {
    private Integer id;
    private String title;
    private String content;
}
