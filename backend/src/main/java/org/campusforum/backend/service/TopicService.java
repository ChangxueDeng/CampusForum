package org.campusforum.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.campusforum.backend.entity.dto.Interact;
import org.campusforum.backend.entity.dto.Topic;
import org.campusforum.backend.entity.dto.TopicType;
import org.campusforum.backend.entity.vo.request.AddCommentVO;
import org.campusforum.backend.entity.vo.request.CreateTopicVO;
import org.campusforum.backend.entity.vo.request.UpdateTopicVO;
import org.campusforum.backend.entity.vo.response.*;
import org.campusforum.backend.exception.ResourceNotFoundException;

import java.util.List;
/**
 * @author ChangxueDeng
 * @date 2024/04/09
 */
public interface TopicService extends IService<Topic> {
    /**
     * 获取帖子类型列表
     * @return {@link List}<{@link TopicType}>
     */
    List<TopicType> typeList();

    /**
     * 按页码和指定的帖子类型获取帖子列表
     * @param page 获取页码
     * @param type 获取帖子类型
     * @return {@link List}<{@link TopicPreviewVO}>
     */
    List<TopicPreviewVO> listTopicByPage(int page, int type);
    /**
     * 创建帖子
     * @param vo 帖子信息
     * @param uid 用户id
     * @return {@link String}
     */
    String createTopic(CreateTopicVO vo, int uid);

    /**
     * 更新帖子
     * @param vo 帖子信息
     * @param uid 用户id
     * @return {@link String}
     */
    String updateTopic(UpdateTopicVO vo, int uid);
    /**
     * 获取置顶帖子列表
     * @return {@link List}<{@link TopicTopVO}>
     */
    List<TopicTopVO> listTopicTop();

    /**
     * 获取帖子详情
     * @param tid 帖子id
     * @param uid 用户id
     * @return {@link TopicDetailVo}
     */
    TopicDetailVo getTopicDetail(int tid, int uid) throws ResourceNotFoundException;

    /**
     * 用户互动
     * 根据类型(like,collect)和状态(true,false)进行点赞收藏操作
     * @param interact 互动参数
     * @param state 互动状态
     */
    void interact(Interact interact, boolean state);

    /**
     * 获取用户的收藏列表
     * @param uid 用户id
     * @return {@link List}<{@link TopicPreviewVO}>
     */
    List<TopicPreviewVO> listCollects(int uid);

    /**
     * 创建评论
     * @param vo 评论参数
     * @param uid 用户id
     * @return {@link String}
     */
    String createComment(AddCommentVO vo, int uid);
    List<CommentVO> listComments(int tid, int page, int id);
    String deleteComment(int cid, int uid);
    public List<SpaceTopicVO> listSpaceTopicByPage(int pageNumber, int uid);
    String followUser(Interact interact, boolean state);
    SpaceVO getSpace(int id, int uid);
    List<FollowVO> getFollows(int id);
    List<FollowVO> getFans(int id);
    List<TopicPreviewVO> listSearchTopics(String keyword, int page, int type);
    String deleteTopic(int id, int uid);
}
