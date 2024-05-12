package org.campusforum.backend.entity.vo.response;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SpaceTopicVO {
    int id;
    int type;
    String title;
    String text;
    List<String> images;
    Date time;
    int like;
    int collect;
    boolean ban;
}
