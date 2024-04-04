package org.campusforum.backend.utils;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class FlowUtils {
    @Resource
    StringRedisTemplate stringRedisTemplate;

    /**
     * 对指定ip进行限流
     * @param key 保护ip地址的key
     * @return boolean
     */
    public boolean limitOnceCheck(String key) {
        if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) return false;
        stringRedisTemplate.opsForValue().set(key, "", 60, TimeUnit.SECONDS);
        return true;
    }
}
