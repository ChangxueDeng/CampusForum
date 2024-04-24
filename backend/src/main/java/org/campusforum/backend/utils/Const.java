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
    public static final long AVATAR_MAXSIZE = 500 * 1024;
    public static final long CACHE_IMAGE_MAX_SIZE = 1024 * 1024 * 5;
    public static final String JSON_CONTENT_TYPE = "application/json";
    public static final String USER_ID = "uid";
    public static final String BLACK_JWT = "black:jwt:";
    public static final String HEAD_TOKEN = "Authorization";
    public static final String LIMIT_EMAIL = "limit:email:";
    public static final String LIMIT_EMAIL_DATA = "limit:email:data:";
    //public static final String LIMIT_FREQUENCY = "limit:frequency:";
    public static final String LIMIT_COUNT_KEY = "limit:count:key:";
    public static final String LIMIT_BAN_KEY = "limit:ban:key:";
    //public static final String BLACK_LIMIT_REQUEST = "black:limit:request:";
    public static final String FORUM_IMAGE_COUNT = "forum:image:";
    public static final String FORUM_TOPIC_COUNT = "forum:topic:";
    public static final String FORUM_TOPIC_PREVIEW_CACHE = "forum:topic:preview:cache:";
}
