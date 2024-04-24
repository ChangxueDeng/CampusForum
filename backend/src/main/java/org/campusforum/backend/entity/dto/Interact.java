package org.campusforum.backend.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * @author ChangxueDeng
 * @date 2024/04/15
 */
@Data
@AllArgsConstructor
public class Interact {
    private Integer tid;
    private Integer uid;
    private String type;
    private Date time;

    public String takeKey() {
        return this.tid + ":" + this.uid;
    }

    public static Interact parseInteract(String key, String type) {
        String[] str = key.split(":");
        return new Interact(Integer.parseInt(str[0]), Integer.parseInt(str[1]), type, new Date());
    }
}
