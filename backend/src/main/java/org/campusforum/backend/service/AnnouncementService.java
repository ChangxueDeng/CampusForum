package org.campusforum.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.campusforum.backend.entity.dto.Announcement;
import org.campusforum.backend.entity.vo.response.AnnouncementVO;

import java.util.List;

public interface AnnouncementService extends IService<Announcement> {
    List<AnnouncementVO> listAnnouncements();
}
