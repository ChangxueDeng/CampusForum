package org.campusforum.backend.entity.vo.response;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

/**
 * 天气信息
 * @author ChangxueDeng
 * @date 2024/04/06
 */
@Data
public class WeatherVO {
    private JSONObject location;
    private JSONObject now;
    private JSONArray hourly;
}
