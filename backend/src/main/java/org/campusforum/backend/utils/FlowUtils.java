package org.campusforum.backend.utils;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 限流工具类
 * 包含：单次请求限制、一段时间内请求次数限制
 * @author ChangxueDeng
 * @date 2024/04/08
 */
@Component
public class FlowUtils {
    @Resource
    StringRedisTemplate stringRedisTemplate;


    /*
    限流流程：
    1. 进行计数
    2. 判断计数是否满足限制
    3. 进行封禁

     */

    /**
     * 单次请求封禁，比如20秒内只能请求1次
     * @param countKey 计数键
     * @param limitTime 限制时间
     * @return boolean 是否成功进行封禁操作
     */
    public boolean limitOnce(String countKey, int limitTime) {
        if (this.checkLimited(countKey)) {
            return true;
        }
        stringRedisTemplate.opsForValue().set(countKey, "", limitTime, TimeUnit.SECONDS);
        return false;
    }

    /**
     * 一段时间内请求进行封禁, 比如30秒内请求次数最多30次, 超过次数则进行封禁一段时间
     * @param countKey 计数键
     * @param limitBanKey 封禁键
     * @param period 计数周期
     * @param frequency 计数频率
     * @param limitTime 封禁时间
     * @return boolean 是否成功进行封禁操作
     */
    public boolean limitPeriod(String countKey, String limitBanKey, int period, int frequency, int limitTime) {
        //判断是否已经封禁
        if (this.checkLimited(limitBanKey)) {
            return true;
        }
        boolean check = this.counterCheck(countKey, period, frequency);
        if (check) {
            //满足封禁条件
            stringRedisTemplate.opsForValue().set(limitBanKey, "", limitTime, TimeUnit.SECONDS);
        }
        return false;
    }

    public boolean limitCountPeriod(String countKey, int period, int frequency) {
        return this.counterCheck(countKey, period, frequency);
    }
    /**
     * 计数验证，从redis中进行计数，并且判断计数是否满足封禁条件
     * @param countKey 计数键
     * @param period 计数周期
     * @param frequency 计数频率
     * @return boolean 是否满足封禁条件
     */
    private boolean counterCheck(String countKey, int period, int frequency) {
        //获取当前计数
        String count = stringRedisTemplate.opsForValue().get(countKey);
        if (count == null) {
            //不存在则添加计数器
            stringRedisTemplate.opsForValue().set(countKey, "1", period, TimeUnit.SECONDS);
            return false;
        } else {
            //存在计数记录，则进行增加
            long counted = Optional.ofNullable(stringRedisTemplate.opsForValue().increment(countKey,1L)).orElse(0L);
            int preCount = Integer.parseInt(count);
            if (counted != preCount + 1) {
                //如何计数出现错误则重新设置
                stringRedisTemplate.opsForValue().set(countKey, "1", period, TimeUnit.SECONDS);
            }
            //满足频率限制则满足封禁条件
            return counted > frequency;
        }
    }



    /**
     * 是否已封禁
     * @param limitBanKey 封禁键
     * @return boolean
     */
    private boolean checkLimited(String limitBanKey) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(limitBanKey));
    }

//    /**
//     * 针对在时间段内多次请求限制
//     * @param counterKey 计数键
//     * @param frequency 请求频率
//     * @param period 周期键
//     * @return boolean 是否通过限流检查
//     */
//    public boolean limitPeriodCounterCheck(String counterKey, int frequency, int period) {
//            return
//    }
}
