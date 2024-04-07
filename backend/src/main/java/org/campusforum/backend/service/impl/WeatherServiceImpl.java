package org.campusforum.backend.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.campusforum.backend.entity.vo.response.WeatherVO;
import org.campusforum.backend.service.WeatherService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

/**
 * 天气接口service实现类
 * @author ChangxueDeng
 * @date 2024/04/06
 */
@Service
public class WeatherServiceImpl implements WeatherService {
    @Value("${weather.apikey}")
    private String apikey;
    @Resource
    RestTemplate rest;
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Override
    public WeatherVO fetchWeather(double longitude, double latitude) {
        return fetchFromCache(longitude, latitude);
    }

    /**
     * 从缓存中获取天气信息
     * @param longitude 经度
     * @param latitude 纬度
     * @return {@link WeatherVO}
     */
    private WeatherVO fetchFromCache(double longitude, double latitude) {
        byte[] data = rest.getForObject("https://geoapi.qweather.com/v2/city/lookup?location="
                + longitude + "," + latitude +"&key=" + apikey, byte[].class);
        //返回数据是JSON格式并进行了Gzip压缩。
        JSONObject geo = decompressString2Json(data);
        //定位
        JSONObject location = null;
        if (geo != null) {
            location = geo.getJSONArray("location").getJSONObject(0);
        }else {
            return null;
        }
        if (location == null) {
            return null;
        } else {
            //获取城市id
            int id = location.getInteger("id");
            String key = "weather" + id;
            //如何缓存中存在信息，直接从redis中获取
            String cache = stringRedisTemplate.opsForValue().get(key);
            if (cache!=null) {
                return JSONObject.parseObject(cache).to(WeatherVO.class);
            }
            //如果缓存中不存在，则通过api获取天气信息
            WeatherVO vo = fetchFromApi(id, location);

            if (vo == null) {
                return null;
            }
            //新获取到的信息存入redis
            stringRedisTemplate.opsForValue().set("key", JSONObject.from(vo).toJSONString(), 1, TimeUnit.HOURS);
            return vo;
        }
    }

    /**
     * 从和风天气api获取天气信息
     * @param id 定位id
     * @param location 定位信息
     * @return {@link WeatherVO}
     */
    private WeatherVO fetchFromApi(int id, JSONObject location){
        WeatherVO vo = new WeatherVO();
        //设置定位信息
        vo.setLocation(location);
        //从天气api获取实时天气
        byte[] data = rest.getForObject("https://devapi.qweather.com/v7/weather/now?location=" + id
                        + "&key=" + apikey, byte[].class);
        JSONObject now = decompressString2Json(data);
        if (now == null) {
            return null;
        }
        vo.setNow(now.getJSONObject("now"));
        //获取24小时天气
        byte[] hData = rest.getForObject("https://devapi.qweather.com/v7/weather/24h?location="
                        + id + "&key=" + apikey, byte[].class);
        JSONObject hourly = decompressString2Json(hData);
        if (hourly == null) {
            return null;
        }
        //只获取前5个
        vo.setHourly(new JSONArray(hourly.getJSONArray("hourly").stream().limit(5).toList()));
        return vo;
    }

    /**
     * 将经过gzip压缩的响应信息进行解压
     * @param data 响应数据
     * @return {@link JSONObject}
     */
    private JSONObject decompressString2Json(byte[] data) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(data));
            //进行读取
            byte[] buffer = new byte[1024];
            int read;
            while ((read = gzipInputStream.read(buffer)) != -1) {
                stream.write(buffer, 0, read);
            }
            gzipInputStream.close();
            stream.close();
            return JSONObject.parseObject(stream.toString());
        }catch (IOException e){
            return null;
        }
    }
}
