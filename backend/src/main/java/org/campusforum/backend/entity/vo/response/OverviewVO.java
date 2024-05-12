package org.campusforum.backend.entity.vo.response;

import lombok.Data;
import java.util.List;

@Data
public class OverviewVO {
    private Long topicCount;
    private List<Long> topicCountByType;
    private Long userCount;
    private Long commentCount;
    private Long notificationCount;
    private Long announcementCount;
    private AnnouncementVO announcement;
}
