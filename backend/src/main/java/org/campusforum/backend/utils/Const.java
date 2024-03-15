package org.campusforum.backend.utils;

import org.springframework.stereotype.Component;

/**
 * 常用字符常量
 * @author ChangxueDeng
 */

@Component
public class Const {

    public static final int ORDER_FILTER_CORS = -102;
    public static final int ORDER_FILTER_LIMIT = -101;
    public static final String USER_ID = "uid";
    public static final String BLACK_JWT = "black:jwt:";
    public static final String HEAD_TOKEN = "Authorization";
    public static final String LIMIT_EMAIL = "limit:email:";
    public static final String LIMIT_EMAIL_DATA = "limit:email:data:";
    public static final String LIMIT_FREQUENCY = "limit:frequency:";
    public static final String BLACK_LIMIT_REQUEST = "black:limit:request:";
}
