package org.campusforum.backend.entity.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * 帖子详请
 * @author ChangxueDeng
 */
@Data
public class TopicDetailVo {
    private Integer id;
    private String title;
    private String content;
    private Integer type;
    Date time;
    User user;
    Interact interact;
    Long commentCount;
    private Boolean ban;
    private Boolean followed;
    private String contentAbstract;
    @Data
    public static class User {
        Integer id;
        String username;
        String avatar;
        String desc;
        Integer gender;
        String qq;
        String wx;
        String phone;
        String email;
    }
    @AllArgsConstructor
    @Data
    public static class Interact{
        Boolean like;
        Boolean collect;
    }

}
