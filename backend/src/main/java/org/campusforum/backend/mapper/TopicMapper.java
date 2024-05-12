package org.campusforum.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.campusforum.backend.entity.dto.Interact;
import org.campusforum.backend.entity.dto.Topic;

import java.util.LinkedList;
import java.util.List;
/**
 * @author ChangxueDeng
 * @date 2024/04/09
 */
@Mapper
public interface TopicMapper extends BaseMapper<Topic> {

    @Delete("delete from topic_interact_${type} where tid = #{tid}")
    void deleteInteractByTid(int tid, String type);
    @Delete("delete from topic_interact_${type} where uid = #{uid}")
    void deleteInteractByUid(int uid, String type);
    @Insert("""
        <script>
            insert ignore into topic_interact_${type}(tid, uid, time) values
            <foreach collection = "interacts" item = "item" separator = ",">
                (#{item.targetId}, #{item.uid}, #{item.time})
            </foreach>
        </script>
    """)
    void addInteract(LinkedList<Interact> interacts, String type);

    @Delete("""
            <script>
                delete from topic_interact_${type} where
                <foreach collection = "interacts" item = "item" separator = " or ">
                    (tid = #{item.targetId} and uid = #{item.uid})
                </foreach>
            </script>
            """)
    void deleteInteract(LinkedList<Interact> interacts, String type);

    @Select("select count(*) from topic_interact_${type} where tid = #{tid} and uid = #{uid}")
    int userInteractCount(int tid, int uid, String type);

    @Select("select count(*) from topic_interact_${type} where tid = #{tid}")
    int interactCount(int tid, String type);

    @Select("""
            select t2.id, t2.title, t2.type from topic_interact_collect t1 left join topic t2 on t1.tid = t2.id
            where t1.uid = #{uid}
            """)
    List<Topic> collects(int uid);
    @Select("select count(*) from topic_interact_like t1 left join topic t2 on t1.tid = t2.id and t2.uid = #{uid}")
    Long userGetLikeCount(int uid);
}
