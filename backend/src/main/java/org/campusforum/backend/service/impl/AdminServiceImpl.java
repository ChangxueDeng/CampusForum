package org.campusforum.backend.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.campusforum.backend.entity.dto.*;
import org.campusforum.backend.entity.vo.request.AddAnnouncementVO;
import org.campusforum.backend.entity.vo.request.AddUserVO;
import org.campusforum.backend.entity.vo.request.UpdateAnnouncement;
import org.campusforum.backend.entity.vo.response.*;
import org.campusforum.backend.mapper.*;
import org.campusforum.backend.service.AdminService;
import org.campusforum.backend.service.ImageService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class AdminServiceImpl implements AdminService {
    @Resource
    private AccountMapper accountMapper;
    @Resource
    private AccountDetailsMapper accountDetailsMapper;
    @Resource
    private TopicMapper topicMapper;
    @Resource
    private TopicCommentMapper topicCommentMapper;
    @Resource
    private TopicTypeMapper topicTypeMapper;
    @Resource
    private NotificationMapper notificationMapper;
    @Resource
    private AnnouncementMapper announcementMapper;
    @Resource
    PasswordEncoder passwordEncoder;
    @Autowired
    private AccountPrivacyMapper accountPrivacyMapper;
    @Resource
    private ImageService imageService;

    @Override
    public OverviewVO getOverview() {
        OverviewVO vo = new OverviewVO();
        vo.setNotificationCount(notificationMapper.selectCount(null));
        vo.setTopicCount(topicMapper.selectCount(null));
        vo.setUserCount(accountMapper.selectCount(null));
        vo.setCommentCount(topicCommentMapper.selectCount(null));
        vo.setAnnouncementCount(announcementMapper.selectCount(null));
        List<Long> topicCountByType = new ArrayList<>();
        //查询各类型帖子的数量
        Long topicTypes = topicTypeMapper.selectCount(null);
        for (int i = 1; i <= topicTypes; i++) {
            topicCountByType.add(topicMapper.selectCount(Wrappers.<Topic>query().eq("type", i)));

        }
        vo.setTopicCountByType(topicCountByType);
        //获取最新的公告
        Announcement announcement = announcementMapper.selectOne(Wrappers.<Announcement>query()
                        .orderByDesc("time").last("limit 1"));
        AnnouncementVO announcementVO = new AnnouncementVO();
        if (announcement != null) {
            announcementVO.setTitle(announcement.getTitle());
            announcementVO.setTime(announcement.getTime());
            announcementVO.setContent(announcement.getContent());
        }
        vo.setAnnouncement(announcementVO);
        return vo;
    }
    @Override
    public List<TopicListVO> listTopicByTyPage(int page) {
        Page<Topic> topicPage = Page.of(page, 10);
        return topicMapper.selectPage(topicPage, null).getRecords().stream().map(t -> {
            TopicListVO vo = new TopicListVO();
            BeanUtils.copyProperties(t, vo);
            return vo;
        }).toList();
    }

    @Override
    public List<AccountListVO> listAccounts(int page) {
        Page<Account> accountPage = Page.of(page, 10);
        Page<AccountDetails> accountDetailsPage = Page.of(page, 10);
        List<Account> accounts = accountMapper.selectPage(accountPage, null).getRecords();
        List<AccountDetails> details = accountDetailsMapper.selectPage(accountDetailsPage, null).getRecords();
        List<AccountListVO> voList = new ArrayList<>();
        for (int i = 0; i < accounts.size(); i++) {
            AccountListVO vo = new AccountListVO();
            BeanUtils.copyProperties(accounts.get(i), vo);
            BeanUtils.copyProperties(details.get(i), vo);
            voList.add(vo);
        }
        return voList;
    }

    @Override
    public List<Notification> listNotificationsByPage(int page) {
        Page<Notification> notificationPage = Page.of(page, 10);
        return notificationMapper.selectPage(notificationPage, null).getRecords();
    }

    @Override
    public List<CommentAdminVO> listCommentsByPage(int page) {
        Page<TopicComment> topicCommentPage = Page.of(page, 10);
        return topicCommentMapper.selectPage(topicCommentPage, null).getRecords().stream().map(t -> {
            CommentAdminVO vo = new CommentAdminVO();
            BeanUtils.copyProperties(t, vo);
            return vo;
        }).toList();
    }

    @Override
    public List<AnnouncementAdminVO> listAnnouncementsByPage(int page) {
        Page<Announcement> announcementPage = Page.of(page, 10);
        return announcementMapper.selectPage(announcementPage, null).getRecords().stream().map(t -> {
            AnnouncementAdminVO vo = new AnnouncementAdminVO();
            BeanUtils.copyProperties(t, vo);
            return vo;
        }).toList();
    }

    @Override
    public void addAnnouncement(AddAnnouncementVO vo, int uid) {
        Announcement announcement = new Announcement();
        announcement.setTitle(vo.getTitle());
        announcement.setContent(vo.getContent());
        announcement.setTime(new Date());
        announcement.setUid(uid);
        announcementMapper.insert(announcement);
    }

    @Override
    public void deleteAnnouncement(int id) {
        announcementMapper.deleteById(id);
    }

    @Override
    public void updateAnnouncement(UpdateAnnouncement vo) {
        announcementMapper.update(null,
                Wrappers.<Announcement>update().
                        eq("id", vo.getId())
                        .set("title", vo.getTitle())
                        .set("content", vo.getContent()));
    }

    @Override
    public String addUser(AddUserVO vo) {
        if (accountMapper.exists(Wrappers.<Account>query().eq("username", vo.getUsername()))) {
            return "用户名已存在";
        } else if (accountMapper.exists(Wrappers.<Account>query().eq("email", vo.getEmail()))) {
            return "邮箱已存在";
        }
        Account account = new Account();
        account.setUsername(vo.getUsername());
        account.setPassword(passwordEncoder.encode(vo.getPassword()));
        account.setRole(vo.getRole());
        account.setRegisterTime(new Date());
        account.setEmail(vo.getEmail());
        accountMapper.insert(account);
        //创建隐私
        AccountPrivacy accountPrivacy = new AccountPrivacy();
        AccountDetails accountDetails = new AccountDetails();
        accountPrivacy.setId(account.getId());
        accountPrivacyMapper.insert(accountPrivacy);
        //创建详细信息
        accountDetails.setId(account.getId());
        accountDetailsMapper.insert(accountDetails);
        return null;
    }

    @Override
    public void deleteComment(int id) {
        topicCommentMapper.delete(Wrappers.<TopicComment>query().eq("id", id));
    }

    @Override
    public void deleteTopic(int id) {
        Topic topic = topicMapper.selectById(id);
        //删除关联评论
        topicCommentMapper.delete(Wrappers.<TopicComment>query().eq("tid", id));
        //删除相关点赞和收藏
        topicMapper.deleteInteractByTid(id, "like");
        topicMapper.deleteInteractByTid(id, "collect");
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
        topicMapper.deleteById(id);
    }


    @Override
    public void banComment(int id, boolean ban) {
        topicCommentMapper.update(null, Wrappers.<TopicComment>update().eq("id", id).set("ban", !ban));
    }

    @Override
    public void banUser(int id, boolean ban) {
        accountMapper.update(null, Wrappers.<Account>update().eq("id", id).set("ban", !ban));
    }

    @Override
    public void banTopic(int id, boolean ban) {
        topicMapper.update(null, Wrappers.<Topic>update().eq("id", id).set("ban", !ban));
    }

    @Override
    public void topTopic(int id, boolean top) {
        topicMapper.update(null, Wrappers.<Topic>update().eq("id", id).set("top", !top));
    }
}

