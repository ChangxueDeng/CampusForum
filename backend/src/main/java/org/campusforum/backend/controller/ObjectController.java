package org.campusforum.backend.controller;

import io.minio.errors.ErrorResponseException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.campusforum.backend.entity.Result;
import org.campusforum.backend.service.ImageService;
import org.campusforum.backend.utils.StatusUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author ChangxueDeng
 * @date 2024/04/06
 */
@Tag(name = "获取文件接口")
@RestController
@Slf4j
public class ObjectController {
    @Resource
    ImageService imageService;

    /**
     * 获取头像
     * @param request 请求
     * @param response 响应
     * @throws Exception 异常
     */
    @Operation(summary = "获取用户头像", description = "从Minio中获取用户头像")
    @GetMapping("/images/**")
    public void imageFetch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        fetchImage(request, response);
    }

    /**
     * 获取图片
     * @param request 请求
     * @param response 响应
     * @throws Exception 异常
     */
    private void fetchImage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //图片路径(/images刚好7个字符)
        String imagePath = request.getServletPath().substring(7);
        //获取输出流
        ServletOutputStream stream = response.getOutputStream();
        //路径过短代表不存在
        if (imagePath.length() <= 13) {
            stream.println(Result.failure(StatusUtils.STATUS_NOT_FOUND, StatusUtils.MESSAGE_FAILURE_NOT_FOUND).toString());
        } else {
            try {
                //从Minio中获取图片
                imageService.fetchImageFromMinio(stream, imagePath);
                //设置缓存时间，避免重复获取图片资源
                response.setHeader("Cache-Control", "max-age=2592000");
            } catch (ErrorResponseException e) {
                //抛出异常时进行状态码判断
                if (e.response().code() == StatusUtils.STATUS_NOT_FOUND) {
                    //设置状态码为404
                    response.setStatus(StatusUtils.STATUS_NOT_FOUND);
                    stream.println(Result.failure(StatusUtils.STATUS_NOT_FOUND, StatusUtils.MESSAGE_FAILURE_NOT_FOUND).toString());
                } else {
                    log.error("从Minio获取图片异常: {}", e.getMessage());
                }
            }
        }
    }
}
