package org.campusforum.backend.service;

import org.campusforum.backend.entity.vo.response.WeatherVO;

/**
 * 天气信息service
 * @author ChangxueDeng
 * @date 2024/04/06
 */
public interface WeatherService {
    /**
     * 获取天气信息
     * @param longitude 经度
     * @param latitude 纬度
     * @return {@link WeatherVO}
     */
    WeatherVO fetchWeather(double longitude, double latitude);
}
