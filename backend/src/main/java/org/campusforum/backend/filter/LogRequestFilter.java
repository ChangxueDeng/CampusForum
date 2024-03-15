package org.campusforum.backend.filter;

import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.campusforum.backend.utils.Const;
import org.campusforum.backend.utils.SnowflakeIdGenerator;
import org.slf4j.MDC;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

/**
 * 请求日志过滤器
 * 将每次请求的情况进行打印
 * @author ChangxueDeng
 */
@Component
@Slf4j
public class LogRequestFilter extends OncePerRequestFilter {

    @Resource
    SnowflakeIdGenerator generator;
    private static final Set<String> IGNORE_URL = Set.of("/swagger-ui", "v3/api-doc/");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (isIgnoreUrl(request.getServletPath())) {
            filterChain.doFilter(request, response);
        }
        long startTime = new Date().getTime();
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        filterChain.doFilter(requestWrapper, responseWrapper);
        requestStart(requestWrapper);
        requestEnd(responseWrapper, startTime);
        responseWrapper.copyBodyToResponse();
    }

    /**
     * 判断请求路径是否在排除项中
     * @param url 请求的路径
     * @return boolean
     */
    private boolean isIgnoreUrl(String url){
        for (String str : IGNORE_URL) {
            if (url.startsWith(str)) return true;
        }
        return false;
    }

    /**
     * 打印请求开始的日志
     * @param requestWrapper request包装类
     */
    private void requestStart(ContentCachingRequestWrapper requestWrapper) {
        //生成雪花Id
        long reqId = generator.getNextId();
        //加入日志
        MDC.put("reqId", String.valueOf(reqId));
        Object object = null;
        if ("application/json".equals(requestWrapper.getContentType())) {
            //获取json格式的参数
            byte[] content = requestWrapper.getContentAsByteArray();
            String requestBody = new String(content, StandardCharsets.UTF_8);
            object = JSONObject.parseObject(requestBody);
        } else {
            //获取表单格式的参数
            JSONObject objectTemp = new JSONObject();
            requestWrapper.getParameterMap().forEach((k, v) -> objectTemp.put(k, v.length > 0 ? v[0] : null));
            object = objectTemp;
        }
        Object uid = requestWrapper.getAttribute(Const.USER_ID);
        //进行打印
        if (uid != null) {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            log.info("请求URL: \"{}\" ({}) | 远程IP地址: {} | 身份: {} (UID: {}) | 角色: {} | 请求参数列表: {}",
                    requestWrapper.getServletPath(), requestWrapper.getMethod(), requestWrapper.getRemoteAddr(),
                    user.getUsername(), uid, user.getAuthorities(), object);
        } else {
            log.info("请求URL: \"{}\" ({}) | 远程IP地址: {} | 身份: 未验证 | 请求参数列表: {}",
                    requestWrapper.getServletPath(), requestWrapper.getMethod(), requestWrapper.getRemoteAddr(), object);
        }
    }

    /**
     * 打印请求结束的日志
     * @param responseWrapper response包装类
     * @param startTime 开始时间
     */
    private void requestEnd(ContentCachingResponseWrapper responseWrapper, long startTime) {
        long time = new Date().getTime() - startTime;
        int status = responseWrapper.getStatus();
        String content = status != 200 ? status + "错误" : new String(responseWrapper.getContentAsByteArray());
        log.info("请求处理耗时: {} ms | 响应结果{}", time, content);
    }
}
