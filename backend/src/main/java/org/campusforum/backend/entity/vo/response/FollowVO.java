package org.campusforum.backend.entity.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FollowVO {
    private Integer id;
    private String username;
    private String avatar;
    private Integer gender;
}
