package org.campusforum.backend.utils;


import org.springframework.stereotype.Component;

/**
 * 常用响应状态
 * @author ChangxueDeng
 */

@Component
public class StatusUtils {
    //请求成功
    public static final int STATUS_OK = 200;
    //错误请求
    public static final int STATUS_BAD_REQUEST = 400;
    //无权限
    public static final int STATUS_UNAUTHORIZED = 401;
    //拒绝服务
    public static final int STATUS_FORBIDDEN = 403;
    //未找到
    public static final int STATUS_NOT_FOUND = 405;

    public static final String MESSAGE_SUCCESS = "请求成功";
    public static final String MESSAGE_FAILURE = "请求失败";
    public static final String MESSAGE_FAILURE_UNAUTHORIZED= "身份未验证";
    public static final String MESSAGE_FAILURE_FORBIDDEN = "权限不足";
    public static final String MESSAGE_FAILURE_BAD_REQUEST = "错误的请求";
    public static final String MESSAGE_FAILURE_NOT_FOUND = "资源未找到";



}
