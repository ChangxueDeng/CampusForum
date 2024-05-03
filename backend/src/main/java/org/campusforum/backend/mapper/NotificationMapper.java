package org.campusforum.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.campusforum.backend.entity.dto.Notification;
@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {
}
