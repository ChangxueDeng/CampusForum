package org.campusforum.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.campusforum.backend.entity.dto.Interact;
import org.campusforum.backend.entity.dto.Topic;
import org.campusforum.backend.entity.dto.TopicType;
import org.campusforum.backend.entity.vo.request.CreateTopicVO;
import org.campusforum.backend.entity.vo.request.UpdateTopicVO;
import org.campusforum.backend.entity.vo.response.TopicDetailVo;
import org.campusforum.backend.entity.vo.response.TopicPreviewVO;
import org.campusforum.backend.entity.vo.response.TopicTopVO;

import java.util.List;
/**
 * @author ChangxueDeng
 * @date 2024/04/09
 */
public interface TopicService extends IService<Topic> {
    /**
     * @return {@link List}<{@link TopicType}>
     */
    List<TopicType> typeList();
    List<TopicPreviewVO> listTopicByPage(int page, int type);
    /**
     * @param vo
     * @param uid
     * @return {@link String}
     */
    String createTopic(CreateTopicVO vo, int uid);

    /**
     * @param vo
     * @param uid
     * @return {@link String}
     */
    String updateTopic(UpdateTopicVO vo, int uid);
    /**
     * @return {@link List}<{@link TopicTopVO}>
     */
    List<TopicTopVO> listTopicTop();

    /**
     * @param tid
     * @param uid
     * @return {@link TopicDetailVo}
     */
    TopicDetailVo getTopicDetail(int tid, int uid);

    /**
     * @param interact
     * @param state
     */
    void interact(Interact interact, boolean state);

    /**
     * @param uid
     * @return {@link List}<{@link TopicPreviewVO}>
     */
    List<TopicPreviewVO> listCollects(int uid);
}
