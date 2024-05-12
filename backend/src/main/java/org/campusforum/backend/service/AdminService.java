package org.campusforum.backend.service;

import org.campusforum.backend.entity.dto.Notification;
import org.campusforum.backend.entity.vo.request.AddAnnouncementVO;
import org.campusforum.backend.entity.vo.request.AddUserVO;
import org.campusforum.backend.entity.vo.request.UpdateAnnouncement;
import org.campusforum.backend.entity.vo.response.*;

import java.util.List;

public interface AdminService {
    List<AccountListVO> listAccounts(int page);
    OverviewVO getOverview();
    List<TopicListVO> listTopicByTyPage(int page);
    List<Notification> listNotificationsByPage(int page);
    List<CommentAdminVO> listCommentsByPage(int page);
    List<AnnouncementAdminVO> listAnnouncementsByPage(int page);
    void addAnnouncement(AddAnnouncementVO vo, int uid);
    void deleteAnnouncement(int id);
    void updateAnnouncement(UpdateAnnouncement vo);
    String addUser(AddUserVO vo);
    void banUser(int id, boolean ban);
    void banTopic(int id, boolean ban);
    void banComment(int id, boolean ban);
    void deleteComment(int id);
    void deleteTopic(int id);
    void topTopic(int id, boolean top);
}
