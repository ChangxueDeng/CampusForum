package org.campusforum.backend.entity.vo.response;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class TopicPreviewVO {
    int id;
    int type;
    String title;
    String text;
    List<String> images;
    Date time;
    Integer uid;
    String username;
    String avatar;
    int like;
    int collect;
    boolean ban;
}
