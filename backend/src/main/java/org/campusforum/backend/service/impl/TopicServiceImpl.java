package org.campusforum.backend.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.campusforum.backend.entity.dto.*;
import org.campusforum.backend.entity.vo.request.AddCommentVO;
import org.campusforum.backend.entity.vo.request.CreateTopicVO;
import org.campusforum.backend.entity.vo.request.UpdateTopicVO;
import org.campusforum.backend.entity.vo.response.CommentVO;
import org.campusforum.backend.entity.vo.response.TopicDetailVo;
import org.campusforum.backend.entity.vo.response.TopicPreviewVO;
import org.campusforum.backend.entity.vo.response.TopicTopVO;
import org.campusforum.backend.mapper.*;
import org.campusforum.backend.service.TopicService;
import org.campusforum.backend.utils.CacheUtils;
import org.campusforum.backend.utils.Const;
import org.campusforum.backend.utils.FlowUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author ChangxueDeng
 */
@Service
public class TopicServiceImpl extends ServiceImpl<TopicMapper, Topic> implements TopicService {
    @Resource
    TopicTypeMapper topicTypeMapper;
    @Resource
    FlowUtils flowUtils;
    @Resource
    CacheUtils cacheUtils;
    @Resource
    AccountMapper accountMapper;
    @Resource
    AccountDetailsMapper accountDetailsMapper;
    @Resource
    AccountPrivacyMapper accountPrivacyMapper;
    @Resource
    TopicCommentMapper topicCommentMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private static final int TOPIC_CONTENT_MAX = 20000;
    private static final int TOPIC_COMMENT_MAX = 2000;
    @Override
    public List<TopicType> typeList() {
        return topicTypeMapper.selectList(null);
    }

    @Override
    public String createTopic(CreateTopicVO vo, int uid) {
        if (contentLimit(vo.getContent(), TOPIC_CONTENT_MAX)) {
            return "主题内容过多，发布失败";
        } else if (!types().contains(vo.getType())) {
            return "主题类型非法";
        } else if (flowUtils.limitCountPeriod(Const.FORUM_TOPIC_COUNT + uid, 3600, 3)) {
            return "发文频繁，稍后再试";
        }
        Topic topic = new Topic();
        BeanUtils.copyProperties(vo, topic);
        topic.setContent(vo.getContent().toJSONString());
        topic.setUid(uid);
        topic.setTime(new Date());
        if (this.save(topic)) {
            for (String keys : Objects.requireNonNull(stringRedisTemplate.keys(Const.FORUM_TOPIC_PREVIEW_CACHE + "*"))) {
                cacheUtils.deleteCache(keys);
            }
            return null;
        } else {
            return "内部错误";
        }
    }

    @Override
    public String updateTopic(UpdateTopicVO vo, int uid) {
        if (contentLimit(vo.getContent(), TOPIC_CONTENT_MAX)) {
            return "主题内容过多，发布失败";
        } else if (!types().contains(vo.getType())){
            return "主题类型非法";
        }
        baseMapper.update(null, new UpdateWrapper<Topic>()
                .eq("id", vo.getId())
                .eq("uid", uid)
                .set("title", vo.getTitle())
                .set("type", vo.getType())
                .set("content", vo.getContent().toJSONString()));
        return null;
    }

    @Override
    public List<TopicPreviewVO> listTopicByPage(int pageNumber, int type) {
        String key = Const.FORUM_TOPIC_PREVIEW_CACHE + pageNumber + ":" + type;
        List<TopicPreviewVO> list = cacheUtils.takeListFormCache(key, TopicPreviewVO.class);
        if (list != null) {
            return list;
        }
        Page<Topic> topicPage = Page.of(pageNumber, 10);
        if (type == 0) {
            baseMapper.selectPage(topicPage, Wrappers.<Topic>query().orderByDesc("time"));
        } else {
            baseMapper.selectPage(topicPage, Wrappers.<Topic>query().eq("type", type).orderByDesc("time"));
        }
        List<Topic> topics = topicPage.getRecords();
        list = topics.stream().map(this::topic2TopicPreviewVO).toList();
        cacheUtils.savaList2Cache(key, list, 60);
        return list;
    }
    private TopicPreviewVO topic2TopicPreviewVO(Topic topic) {
        TopicPreviewVO vo = new TopicPreviewVO();
        BeanUtils.copyProperties(topic, vo);
        Account account = accountMapper.selectById(topic.getUid());
        vo.setUsername(account.getUsername());
        vo.setAvatar(account.getAvatar());
        vo.setLike(baseMapper.InteractCount(topic.getId(), "like"));
        vo.setCollect(baseMapper.InteractCount(topic.getId(), "collect"));
        List<String> image = new ArrayList<>();
        StringBuilder previewText = new StringBuilder();
        JSONArray ops = JSONObject.parseObject(topic.getContent()).getJSONArray("ops");
        this.shortContent(ops, previewText, obj -> image.add(obj.toString()));
        vo.setText(previewText.length() > 300 ? previewText.substring(0, 300) : previewText.toString());
        vo.setImages(image);
        return vo;
    }

    private void shortContent(JSONArray ops, StringBuilder previewText, Consumer<Object> imageHandler) {
        for (Object obj : ops) {
            Object insert = JSONObject.from(obj).get("insert");
            if (insert instanceof String text) {
                if (previewText.length() < 300) {
                    previewText.append(text);
                }
            } else if (insert instanceof Map<?, ?> map) {
                Optional.ofNullable(map.get("image")).ifPresent(imageHandler);
            }
        }
    }

    @Override
    public List<TopicTopVO> listTopicTop() {
        List<Topic> topics = this.list(new QueryWrapper<Topic>().select("id", "title", "time").eq("top", 1));
        return topics.stream().map(topic -> new TopicTopVO(topic.getId(), topic.getTitle(), topic.getTime())).toList();
    }

    @Override
    public TopicDetailVo getTopicDetail(int tid, int uid) {
        TopicDetailVo vo = new TopicDetailVo();
        Topic topic = baseMapper.selectById(tid);
        BeanUtils.copyProperties(topic, vo);
        TopicDetailVo.Interact interact = new TopicDetailVo.Interact(
                hasInteract(tid, uid, "like"),
                hasInteract(tid, uid, "collect")
        );
        vo.setInteract(interact);
        TopicDetailVo.User user = new TopicDetailVo.User();
        vo.setUser(fillUserDetailsByPrivacy(user, topic.getUid()));
        vo.setCommentCount(topicCommentMapper.selectCount(Wrappers.<TopicComment>query().eq("tid", tid)));
        return vo;
    }
    /**
     * 根据用户隐私设置填充用户详细信息。
     * 该方法会根据指定用户的ID，查询其账户、账户详情和隐私设置，然后将非隐藏的信息填充到目标对象中。
     *
     * @param target 目标对象，用于填充用户信息。
     * @param uid 用户ID，用于查询用户的账户、详情和隐私设置。
     * @return 填充了用户信息的目標对象。
     * @param <T> 目标对象的类型。
     */
    private <T> T fillUserDetailsByPrivacy(T target, int uid) {
        // 根据用户ID查询账户信息
        Account account = accountMapper.selectById(uid);
        // 根据用户ID查询账户详情
        AccountDetails accountDetails = accountDetailsMapper.selectById(uid);
        // 根据用户ID查询账户隐私设置
        AccountPrivacy accountPrivacy = accountPrivacyMapper.selectById(uid);
        // 分析隐私设置，获取需要隐藏的字段
        String[] strings = null;
        if (accountPrivacy != null) {
            strings= accountPrivacy.hiddenFields();

        }
        // 根据隐私设置，将账户和账户详情中需要隐藏的字段值复制到目标对象
        BeanUtils.copyProperties(account, target, strings);
        BeanUtils.copyProperties(accountDetails, target, strings);

        return target;
    }

    @Override
    public void interact(Interact interact, boolean state) {
        String type = interact.getType();
        synchronized (type.intern()) {
            stringRedisTemplate.opsForHash().put(type, interact.takeKey(), Boolean.toString(state));
            this.saveInteractSchedule(type);
        }
    }

    private final Map<String, Boolean> scheduleState = new HashMap<>();
    ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);
    private void saveInteractSchedule(String type) {
        if (!scheduleState.getOrDefault(type, false)) {
            scheduleState.put(type, true);
            scheduledExecutorService.schedule(() -> {
                    this.saveInteract(type);
                scheduleState.put(type, false);
            }, 3, TimeUnit.SECONDS);
        }
    }
    private void saveInteract(String type) {
        synchronized (type.intern()) {
            LinkedList<Interact> check = new LinkedList<>();
            LinkedList<Interact> uncheck = new LinkedList<>();
            stringRedisTemplate.opsForHash().entries(type).forEach((k, v) -> {
                if (Boolean.parseBoolean(v.toString())) {
                    check.add(Interact.parseInteract(k.toString(), type));
                } else {
                    uncheck.add(Interact.parseInteract(k.toString(),type));
                }
                if (!check.isEmpty()) {
                    baseMapper.addInteract(check, type);
                }
                if (!uncheck.isEmpty()) {
                    baseMapper.deleteInteract(uncheck, type);
                }
                stringRedisTemplate.delete(type);
            });
        }
    }
    private boolean hasInteract(int tid, int uid, String type) {
        String key = tid + ":" + uid;
        if (stringRedisTemplate.opsForHash().hasKey(type, key)) {
            return Boolean.parseBoolean(stringRedisTemplate.opsForHash().entries(type).get(key).toString());
        }
        return baseMapper.userInteractCount(tid, uid, type) > 0;
    }

    @Override
    public List<TopicPreviewVO> listCollects(int uid) {
       return baseMapper.collects(uid).stream().map(topic -> {
            TopicPreviewVO vo = new TopicPreviewVO();
            BeanUtils.copyProperties(topic, vo);
            return vo;
        }).toList();
    }

    private boolean contentLimit(JSONObject object, int max) {
        if (object == null) {
            return false;
        }
        long len = 0L;
        for (Object obj : object.getJSONArray("ops")) {
            Object insert = JSONObject.from(obj).get("insert");
            if (insert instanceof String text) {
                len += text.length();
            }
        }
        return len > max;
    }

    private Set<Integer> types() {
        return topicTypeMapper.selectList(null)
                .stream().
                map(TopicType::getId)
                .collect(Collectors.toSet());
    }

    @Override
    public String createComment(AddCommentVO vo, int uid) {
        if (contentLimit(JSONObject.parseObject(vo.getContent()), TOPIC_COMMENT_MAX)) {
            return "评论内容过多，发表失败";
        } else if (flowUtils.limitCountPeriod(Const.FORUM_TOPIC_COMMENT_COUNT + uid, 60, 2)) {
            return "评论频繁，请稍后再试";
        }
        TopicComment comment = new TopicComment();
        comment.setTid(vo.getTid());
        comment.setUid(uid);
        comment.setContent(vo.getContent());
        comment.setTime(new Date());
        comment.setQuote(vo.getQuote());
        topicCommentMapper.insert(comment);
        return null;
    }

    @Override
    public List<CommentVO> listComments(int tid, int page) {
        Page<TopicComment> commentPage = Page.of(page, 10);
        topicCommentMapper.selectPage(commentPage, Wrappers.<TopicComment>query().eq("tid", tid));
        return commentPage.getRecords().stream().map(dto -> {
            CommentVO vo = new CommentVO();
            BeanUtils.copyProperties(dto, vo);
            if(dto.getQuote() > 0) {
                TopicComment comment = topicCommentMapper.selectOne(Wrappers
                        .<TopicComment>query()
                        .eq("id", dto.getQuote())
                        .orderByAsc("time"));
                if (comment != null) {
                    JSONObject object = JSONObject.parseObject(comment.getContent());
                    StringBuilder builder = new StringBuilder();
                    this.shortContent(object.getJSONArray("ops"), builder, ignore -> {} );
                    vo.setQuote(builder.toString());
                } else {
                    vo.setQuote("此评论已被删除");
                }

            }
            CommentVO.User user = new CommentVO.User();
            this.fillUserDetailsByPrivacy(user, dto.getUid());
            vo.setUser(user);
            return vo;
        }).toList();
    }

    @Override
    public String deleteComment(int cid, int uid) {
        try {
            topicCommentMapper.delete(Wrappers.<TopicComment>query().eq("id", cid).eq("uid", uid));

        } catch (Exception e) {
            return "操作错误";
        }
        return null;
    }
}
