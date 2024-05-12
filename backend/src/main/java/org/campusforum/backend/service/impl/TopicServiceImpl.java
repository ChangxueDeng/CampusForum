package org.campusforum.backend.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.swagger.v3.oas.models.security.SecurityScheme;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.campusforum.backend.entity.dto.*;
import org.campusforum.backend.entity.vo.request.AddCommentVO;
import org.campusforum.backend.entity.vo.request.CreateTopicVO;
import org.campusforum.backend.entity.vo.request.UpdateTopicVO;
import org.campusforum.backend.entity.vo.response.*;
import org.campusforum.backend.exception.ResourceNotFoundException;
import org.campusforum.backend.mapper.*;
import org.campusforum.backend.service.AccountFollowsService;
import org.campusforum.backend.service.ImageService;
import org.campusforum.backend.service.NotificationService;
import org.campusforum.backend.service.TopicService;
import org.campusforum.backend.utils.CacheUtils;
import org.campusforum.backend.utils.Const;
import org.campusforum.backend.utils.FlowUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author ChangxueDeng
 */
@Slf4j
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
    NotificationService notificationService;
    @Resource
    private AccountFollowsMapper accountFollowsMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private ImageService imageService;

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
    @Override
    public List<SpaceTopicVO> listSpaceTopicByPage(int pageNumber, int uid) {
        Page<Topic> topicPage = Page.of(pageNumber, 10);
        return this.page(topicPage, Wrappers.<Topic>query().eq("uid", uid))
                .getRecords().stream().map(this::topic2SpaceTopicVO).toList();
    }
    private SpaceTopicVO topic2SpaceTopicVO(Topic topic) {
        SpaceTopicVO vo = new SpaceTopicVO();
        List<String> images = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        BeanUtils.copyProperties(topic, vo);
        JSONArray ops = JSONObject.parseObject(topic.getContent()).getJSONArray("ops");
        this.shortContent(ops, builder, obj -> images.add(obj.toString()));
        vo.setText(builder.length() > 300 ? builder.substring(0, 300) : builder.toString());
        vo.setImages(images);
        vo.setLike(baseMapper.interactCount(topic.getId(), "like"));
        vo.setCollect(baseMapper.interactCount(topic.getId(), "collect"));
        return vo;
    }
    private TopicPreviewVO topic2TopicPreviewVO(Topic topic) {
        TopicPreviewVO vo = new TopicPreviewVO();
        if (!topic.isBan()) {
            BeanUtils.copyProperties(topic, vo);
            List<String> image = new ArrayList<>();
            StringBuilder previewText = new StringBuilder();
            JSONArray ops;
            ops = JSONObject.parseObject(topic.getContent()).getJSONArray("ops");
            this.shortContent(ops, previewText, obj -> image.add(obj.toString()));
            vo.setText(previewText.length() > 300 ? previewText.substring(0, 300) : previewText.toString());
            vo.setImages(image);
        } else {
            vo.setBan(true);
            BeanUtils.copyProperties(topic, vo, "content");
        }
        Account account = accountMapper.selectById(topic.getUid());
        vo.setUsername(account.getUsername());
        vo.setAvatar(account.getAvatar());
        vo.setLike(baseMapper.interactCount(topic.getId(), "like"));
        vo.setCollect(baseMapper.interactCount(topic.getId(), "collect"));
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
    public TopicDetailVo getTopicDetail(int tid, int uid) throws ResourceNotFoundException {
        TopicDetailVo vo = new TopicDetailVo();
        Topic topic = baseMapper.selectById(tid);
        if (topic == null) {
            throw new ResourceNotFoundException("帖子不存在");
        }
        //如果被封禁则返回设置ban为true，并且直接返回
        if (topic.isBan()) {
            vo.setBan(true);
            return vo;
        }
        BeanUtils.copyProperties(topic, vo);
        TopicDetailVo.Interact interact = new TopicDetailVo.Interact(
                hasInteract(tid, uid, "like"),
                hasInteract(tid, uid, "collect")
        );
        vo.setInteract(interact);
        TopicDetailVo.User user = new TopicDetailVo.User();
        vo.setUser(fillUserDetailsByPrivacy(user, topic.getUid()));
        vo.setCommentCount(topicCommentMapper.selectCount(Wrappers.<TopicComment>query().eq("tid", tid)));
        //查看是否关注
         vo.setFollowed(hasInteract(topic.getUid(), uid, "follow"));
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
    @Override
    public String followUser(Interact interact, boolean state) {
        if (!accountMapper.exists(Wrappers.<Account>query().eq("id", interact.getTargetId()))) {
            return "目标用户不存在";
        }
        this.interact(interact, state);
        return null;
    }

    private final Map<String, Boolean> scheduleState = new HashMap<>();
    ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(3);
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
                if ("like".equals(type) || "collect".equals(type))
                {
                    if (!check.isEmpty()) {
                        log.error("保存like");
                        baseMapper.addInteract(check, type);
                    }
                    if (!uncheck.isEmpty()) {
                        log.error("删除like");
                        baseMapper.deleteInteract(uncheck, type);
                    }
                }else if ("follow".equals(type)) {
                    if (!check.isEmpty()) {
                        log.error("保存follow");
                        accountFollowsMapper.addFollows(check, type);
                    }
                    if (!uncheck.isEmpty()) {
                        log.error("删除follow");
                        accountFollowsMapper.deleteFollows(uncheck, type);
                    }
                }
                stringRedisTemplate.delete(type);
            });
        }
    }
    private boolean hasInteract(int targetId, int uid, String type) {
        String key = targetId + ":" + uid;
        if (stringRedisTemplate.opsForHash().hasKey(type, key)) {
            return Boolean.parseBoolean(stringRedisTemplate.opsForHash().entries(type).get(key).toString());
        }
        if ("follow".equals(type)) {
            return accountFollowsMapper.exists(Wrappers.<AccountFollows>query()
                    .eq("follower", targetId).eq("fans",uid));
        }
        return baseMapper.userInteractCount(targetId, uid, type) > 0;
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
        Topic topic = this.getById(vo.getTid());
        if (topic.isBan()) {
            return "帖子已被封禁，无法发表评论";
        } else if (vo.getQuote() > 0) {
            if (!topicCommentMapper.exists(Wrappers.<TopicComment>query().eq("id", vo.getQuote()))) {
                return "评论已被删除，无法发表评论";
            } else if (!topicCommentMapper.exists(Wrappers.<TopicComment>query().eq("id", vo.getTid()).eq("tid", vo.getTid()))) {
                return "不存在这条评论";
            } else if (topicCommentMapper.selectById(vo.getQuote()).isBan()) {
                return "评论已被封禁，无法发表评论";
            }
        } else if (contentLimit(JSONObject.parseObject(vo.getContent()), TOPIC_COMMENT_MAX)) {
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
        Account account = accountMapper.selectById(uid);
        if (vo.getQuote() > 0) {
            TopicComment comment1 = topicCommentMapper.selectById(vo.getQuote());
            if (!Objects.equals(account.getId(), comment1.getUid())) {
                notificationService.addNotification(
                        comment1.getUid(),
                        "您在帖子" + topic.getTitle() + "中的评论被回复",
                        account.getUsername() + "回复了你发表的评论，请查看",
                        "success",
                        "/index/topic-detail/" + topic.getId()
                );
            }
        } else if (!Objects.equals(account.getId(), topic.getUid())) {
            notificationService.addNotification(
                    topic.getUid(),
                    "您发表的帖子被评论",
                    account.getUsername() + "评论了你发表的帖子: " + topic.getTitle() + "，请查看",
                    "success",
                    "/index/topic-detail/" + topic.getId()
            );
        }
        return null;
    }

    @Override
    public List<CommentVO> listComments(int tid, int page, int id) {
        Page<TopicComment> commentPage = Page.of(page, 10);
        topicCommentMapper.selectPage(commentPage, Wrappers.<TopicComment>query().eq("tid", tid));
        return commentPage.getRecords().stream().map(dto -> {
            CommentVO vo = new CommentVO();
            if (dto.isBan()) {
                vo.setBan(true);
                BeanUtils.copyProperties(dto, vo, "content");
            } else {
                BeanUtils.copyProperties(dto, vo);
            }
            if(dto.getQuote() > 0) {
                TopicComment comment = topicCommentMapper.selectById(dto.getQuote());
                if (comment != null) {
                    if (comment.isBan()) {
                        vo.setQuote("此评论已被封禁");
                    } else {
                        JSONObject object = JSONObject.parseObject(comment.getContent());
                        StringBuilder builder = new StringBuilder();
                        this.shortContent(object.getJSONArray("ops"), builder, ignore -> {} );
                        vo.setQuote(builder.toString());
                    }
                }else {
                    vo.setQuote("此评论已被删除");
                }

            }
            CommentVO.User user = new CommentVO.User();
            this.fillUserDetailsByPrivacy(user, dto.getUid());
            vo.setUser(user);
            //查看是否关注发布评论者
            vo.setFollowed(this.hasInteract(dto.getUid(), id, "follow"));
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

    @Override
    public SpaceVO getSpace(int id, int uid) {
        //获取用户基本信息
        Account account = accountMapper.selectById(id);
        if (account == null) {
            return null;
        }
        SpaceVO vo = new SpaceVO();
        vo.setId(account.getId());
        vo.setUsername(account.getUsername());
        vo.setAvatar(account.getAvatar());
        //获取详细信息
        AccountDetails accountDetails = accountDetailsMapper.selectById(id);
        if (accountPrivacyMapper.selectById(id).isGender()) {
            vo.setGender(accountDetails.getGender());
        }
        vo.setDesc(accountDetails.getDesc());
        //获取关注数
        vo.setFollow(accountFollowsMapper.selectCount(Wrappers.<AccountFollows>query().eq("fans", id)));
        //获取粉丝数
        vo.setFans(accountFollowsMapper.selectCount(Wrappers.<AccountFollows>query().eq("follower", id)));
        //获取获得的点赞数
        vo.setLike(baseMapper.userGetLikeCount(id));
        vo.setFollowed(this.hasInteract(id, uid, "follow"));
        return vo;
    }

    @Override
    public List<FollowVO> getFollows(int id) {
        return accountFollowsMapper.selectList(Wrappers.<AccountFollows>query().eq("fans", id)).stream().map(f -> {
            Account account = accountMapper.selectById(f.getFollower());
            AccountDetails details = accountDetailsMapper.selectById(f.getFollower());
            return new FollowVO(account.getId(), account.getUsername(), account.getAvatar(), details.getGender());
        }).toList();
    }

    @Override
    public List<FollowVO> getFans(int id) {
        return accountFollowsMapper.selectList(Wrappers.<AccountFollows>query().eq("follower", id)).stream().map(f -> {
            Account account = accountMapper.selectById(f.getFans());
            AccountDetails details = accountDetailsMapper.selectById(f.getFans());
            return new FollowVO(account.getId(), account.getUsername(), account.getAvatar(), details.getGender());
        }).toList();
    }

    @Override
    public List<TopicPreviewVO> listSearchTopics(String keyword, int page, int type) {
        Page<Topic> topicPage = Page.of(page, 10);
        if (type == 0) {
            this.page(topicPage, Wrappers.<Topic>query().like("title", keyword));
        } else {
            this.page(topicPage, Wrappers.<Topic>query().eq("type", type).like("title", keyword));
        }
        return topicPage.getRecords().stream().map(this::topic2TopicPreviewVO).toList();
    }

    @Override
    public String deleteTopic(int id, int uid) {
        Topic topic = this.getOne(Wrappers.<Topic>query().eq("id", id).eq("uid", uid));
        if (topic == null) {
            return "帖子不存在或无权限";
        }
        //删除关联评论
        topicCommentMapper.delete(Wrappers.<TopicComment>query().eq("tid", id));
        //删除相关点赞和收藏
        baseMapper.deleteInteractByTid(id, "like");
        baseMapper.deleteInteractByTid(id, "collect");
        //获取帖子中的图片
        List<String> images = new ArrayList<>();
        JSONArray ops = JSONObject.parseObject(topic.getContent()).getJSONArray("ops");
        for (Object object : ops) {
            JSONObject op = JSONObject.from(object);
            if (op.containsKey("insert") && op.get("insert") instanceof JSONObject img) {
                images.add(img.getString("image"));
            }
        }
        images.forEach(i -> {
            try {
                imageService.deleteCache(i);
            } catch (Exception e) {
                log.warn("删除图片错误:{}", e.getMessage());
            }
        });
        //删除帖子
        this.remove(Wrappers.<Topic>query().eq("id", id).eq("uid", uid));
        return null;
    }
}
