package org.campusforum.backend.entity.vo.response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ChangxueDeng
 * @date 2024/04/09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopicTypeVO {
    private int id;
    private String name;
    private String desc;
    private String color;
}
