package org.campusforum.backend.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.campusforum.backend.entity.dto.Announcement;
import org.campusforum.backend.entity.vo.response.AnnouncementVO;
import org.campusforum.backend.mapper.AnnouncementMapper;
import org.campusforum.backend.service.AnnouncementService;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements AnnouncementService {
    @Override
    public List<AnnouncementVO> listAnnouncements() {
        return this.list(Wrappers.<Announcement>query().orderByDesc("time").last("limit 10")).stream().map(announcement -> {
            AnnouncementVO vo = new AnnouncementVO();
            vo.setTitle(announcement.getTitle());
            vo.setContent(announcement.getContent());
            vo.setTime(announcement.getTime());
            return vo;
        }).toList();
    }
}
