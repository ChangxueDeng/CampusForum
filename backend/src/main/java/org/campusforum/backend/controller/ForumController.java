package org.campusforum.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.campusforum.backend.entity.Result;
import org.campusforum.backend.entity.vo.response.WeatherVO;
import org.campusforum.backend.service.WeatherService;
import org.campusforum.backend.utils.StatusUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 论坛主体功能Controller
 * @author ChangxueDeng
 * @date 2024/04/06
 */
@RestController
@ResponseBody
@Tag(name = "论坛主体功能接口")
@RequestMapping("/api/forum")
public class ForumController {
    @Resource
    WeatherService weatherService;

    /**
     * 获取天气信息
     * @param longitude 经度
     * @param latitude 纬度
     * @return {@link Result}<{@link WeatherVO}>
     */
    @Operation(summary = "获取天气信息", description = "通过经纬度，从和风天气api获取天气信息")
    @GetMapping("/weather")
    public Result<WeatherVO> getWeather(
            @Parameter(description = "经度", content = @Content(schema = @Schema(implementation = double.class)))
            @RequestParam("longitude") double longitude,
            @Parameter(description = "纬度", content = @Content(schema = @Schema(implementation = double.class)))
            @RequestParam("latitude") double latitude) {
        WeatherVO weather = weatherService.fetchWeather(longitude, latitude);
        if (weather == null) {
            return Result.failure(StatusUtils.STATUS_BAD_REQUEST, "获取天气和地理位置信息失败，请联系管理员");
        }
        return Result.success(weather);
    }
}
