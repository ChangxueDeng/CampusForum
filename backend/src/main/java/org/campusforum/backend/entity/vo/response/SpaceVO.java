package org.campusforum.backend.entity.vo.response;

import lombok.Data;

/**
 * 个人空间对象
 * 包含用户id, 性别，简介，头像，获赞数，关注的用户数，粉丝数，收藏数
 * @author ChangxueDeng
 * @date 2024/05/08
 */
@Data
public class SpaceVO {
    private Integer id;
    private Integer gender;
    private String username;
    private String desc;
    private String avatar;
    private Long like;
    private Long follow;
    private Long fans;
    private Boolean followed;
}
