package org.campusforum.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.campusforum.backend.entity.dto.AccountFollows;
import org.campusforum.backend.entity.dto.Interact;

import java.util.LinkedList;

@Mapper
public interface AccountFollowsMapper extends BaseMapper<AccountFollows> {
    @Insert("""
            <script>
            insert ignore into account_follows(follower, fans,time) values
            <foreach collection = "interacts" item = "item" separator = ",">
                (#{item.targetId}, #{item.uid}, #{item.time})
            </foreach>
        </script>
            """)
    void addFollows(LinkedList<Interact> interacts, String type);

    @Delete("""
            <script>
                delete from account_follows where
                <foreach collection = "interacts" item = "item" separator = " or ">
                    (follower = #{item.targetId} and fans = #{item.uid})
                </foreach>
            </script>
            """)
    void deleteFollows(LinkedList<Interact> interacts, String type);
}
