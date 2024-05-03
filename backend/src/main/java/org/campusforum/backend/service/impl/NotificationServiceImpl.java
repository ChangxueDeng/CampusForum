package org.campusforum.backend.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.campusforum.backend.entity.dto.Notification;
import org.campusforum.backend.entity.vo.response.NotificationVO;
import org.campusforum.backend.mapper.NotificationMapper;
import org.campusforum.backend.service.NotificationService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author ChangxueDeng
 * @date 2024/05/02
 */
@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    @Override
    public List<NotificationVO> findUserNotifications(int uid) {
        return this.list(Wrappers.<Notification>query().eq("uid", uid)).stream().map(i -> {
            NotificationVO vo = new NotificationVO();
            BeanUtils.copyProperties(i, vo);
            return vo;
        }).toList();
    }

    @Override
    public void deleteUserNotification(int id, int uid) {
        this.remove(Wrappers.<Notification>query().eq("id", id).eq("uid", uid));
    }

    @Override
    public void deleteUserAlNotifications(int uid) {
        this.remove(Wrappers.<Notification>query().eq("uid", uid));
    }

    @Override
    public void addNotification(int uid, String title, String content, String type, String url) {
        Notification notification = new Notification();
        notification.setUid(uid);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setUrl(url);
        notification.setTime(new Date());
        this.save(notification);
    }
}
