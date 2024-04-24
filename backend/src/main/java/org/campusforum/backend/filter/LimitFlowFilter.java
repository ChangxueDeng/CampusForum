package org.campusforum.backend.filter;

import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.campusforum.backend.utils.Const;
import org.campusforum.backend.utils.FlowUtils;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 请求限流过滤器
 * 防止频繁请求接口
 * @author ChangxueDeng
 */

@Component
@Order(Const.ORDER_FILTER_LIMIT)
public class LimitFlowFilter extends OncePerRequestFilter {
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    FlowUtils flowUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!tryCount(request.getRemoteAddr())) {
            filterChain.doFilter(request, response);
        } else {
            blackWriter(response);
        }
    }

    private void blackWriter(HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write("操作频繁，请稍后重试");
    }

    private boolean tryCount(String ip) {
        synchronized (ip.intern()) {
            String countKey = Const.LIMIT_COUNT_KEY + ip;
            String limitBanKey = Const.LIMIT_BAN_KEY + ip;
            int period = 30;
            int frequency = 200;
            int limitTime = 60;
            return flowUtils.limitPeriod(countKey, limitBanKey, period, frequency, limitTime);
        }
    }

//    private boolean limitPeriodCheck(String ip) {
//        String frequency = Const.LIMIT_FREQUENCY + ip;
//        String black = Const.BLACK_LIMIT_REQUEST + ip;
//        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(frequency))) {
//            long count = Optional.ofNullable(stringRedisTemplate.opsForValue().increment(frequency, 1L)).orElse(0L);
//            if (count >= 20) {
//                stringRedisTemplate.opsForValue().set(black, "", 60, TimeUnit.SECONDS);
//            }
//        } else {
//            stringRedisTemplate.opsForValue().set(frequency, "1", 20, TimeUnit.SECONDS);
//        }
//        return true;
//    }
}
