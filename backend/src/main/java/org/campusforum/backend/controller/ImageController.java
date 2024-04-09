package org.campusforum.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.campusforum.backend.entity.Result;
import org.campusforum.backend.service.ImageService;
import org.campusforum.backend.utils.Const;
import org.campusforum.backend.utils.StatusUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author ChangxueDeng
 * @date 2024/04/05
 */

@RestController
@RequestMapping("/api/image")
@Slf4j
@Tag(name = "图片接口")
public class ImageController {

    @Resource
    ImageService imageService;

    /**
     * 上传头像
     * @param file 头像文件
     * @param id 用户id
     * @return {@link Result}<{@link String}>
     * @throws IOException 异常
     */
    @Operation(summary = "上传头像", description = "上传头像到Minio")
    @PostMapping("/avatar")
    public Result<String> uploadAvatar(
            @Parameter(description = "头像文件", content = @Content(schema = @Schema(implementation = MultipartFile.class)))
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "用户id", content = @Content(schema = @Schema(implementation = int.class)))
            @RequestAttribute(Const.USER_ID) int id) throws IOException {
        if (file.getSize() > Const.AVATAR_MAXSIZE) {
            return Result.failure(StatusUtils.STATUS_BAD_REQUEST, "头像大小不能大于500KB");
        } else {
            log.info("正在进行头像上传....");
            String url = imageService.uploadAvatar(file, id);
            if (url != null) {
                log.info("头像上传成功，大小: {}", file.getSize());
                return Result.success(url);
            } else {
                return Result.failure(StatusUtils.STATUS_BAD_REQUEST, "头像上传失败，联系管理员");
            }
        }
    }

    /**
     * 上传贴子图片
     * @param file 图片文件
     * @param id 用户id
     * @return {@link Result}<{@link String}>
     * @throws IOException IO异常
     */
    @PostMapping("/cache")
    public Result<String> uploadCache(
            @Parameter(description = "图片文件", content = @Content(schema = @Schema(implementation = MultipartFile.class)))
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "用户id", content = @Content(schema = @Schema(implementation = int.class)))
            @RequestAttribute(Const.USER_ID) int id) throws IOException {
        if (file.getSize() > Const.CACHE_IMAGE_MAX_SIZE) {
            return Result.failure(StatusUtils.STATUS_BAD_REQUEST, "图片大小不能大于5MB");
        } else {
            log.info("正在进行图片上传");
            String url = imageService.uploadCache(file, id);
            if (url != null) {
                log.info("图片上传成功, 大小： {}", file.getSize());
                return Result.success(url);
            } else {
                return Result.failure(StatusUtils.STATUS_BAD_REQUEST, "图片上传失败，联系管理员");
            }
        }

    }
}
