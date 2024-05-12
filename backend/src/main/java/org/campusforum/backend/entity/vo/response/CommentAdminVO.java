package org.campusforum.backend.entity.vo.response;


import lombok.Data;

import java.util.Date;
@Data
public class CommentAdminVO {
    private Integer id;
    private Integer tid;
    private Integer uid;
    private String content;
    private Date time;
    private Integer quote;
    private Boolean ban;
}
