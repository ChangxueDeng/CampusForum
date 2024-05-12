package org.campusforum.backend.entity.vo.response;

import lombok.Data;

import java.util.Date;

@Data
public class CommentVO {
    private Integer id;
    private String content;
    private Date time;
    private String quote;
    private User user;
    private Boolean ban;
    private Boolean followed;
    @Data
    public static class User {
        private Integer id;
        private String username;
        private String avatar;
        private Integer gender;
        private String qq;
        private String wx;
        private String phone;
        private String email;
    }
}
