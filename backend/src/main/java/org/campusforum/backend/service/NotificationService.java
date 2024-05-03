package org.campusforum.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.campusforum.backend.entity.dto.Notification;
import org.campusforum.backend.entity.vo.response.NotificationVO;

import java.util.List;

public interface NotificationService extends IService<Notification> {
    List<NotificationVO> findUserNotifications(int uid);
    void deleteUserNotification(int id, int uid);
    void deleteUserAlNotifications(int uid);
    void addNotification(int uid, String title, String content, String type, String url);
}
