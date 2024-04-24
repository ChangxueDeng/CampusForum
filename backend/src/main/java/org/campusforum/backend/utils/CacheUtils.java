package org.campusforum.backend.utils;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class CacheUtils {
    @Resource
    StringRedisTemplate stringRedisTemplate;
    public <T> List<T> takeListFormCache(String ket, Class<T> itemType) {
        String str = stringRedisTemplate.opsForValue().get(ket);
        if (str == null) {
            return null;
        }
        return JSONArray.parseArray(str).toList(itemType);
    }

    public <T> T takeFormCache(String key, Class<T> itemType) {
        String str = stringRedisTemplate.opsForValue().get(key);
        if (str == null) {
            return null;
        }
        return JSONObject.parseObject(str).to(itemType);
    }

    public <T> void sava2Cache(String key, T data, long expire) {
        stringRedisTemplate.opsForValue().set(key, JSONObject.from(data).toJSONString(), expire, TimeUnit.SECONDS);
    }
    public <T> void savaList2Cache(String key, List<T> list, long expire) {
        stringRedisTemplate.opsForValue().set(key, JSONArray.from(list).toJSONString(), expire, TimeUnit.SECONDS);
    }
    public void deleteCache(String key) {
        stringRedisTemplate.delete(key);
    }
}
