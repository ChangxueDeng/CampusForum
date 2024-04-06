package org.campusforum.backend.service;

import io.minio.errors.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
/**
 * @author ChangxueDeng
 * @date 2024/04/06
 */
public interface ImageService {
    /**
     * 将图像上传到Minio
     * @param file 图片文件
     * @param id 用户id
     * @return {@link String}
     * @throws IOException 异常
     */
    String uploadAvatar(MultipartFile file, int id) throws IOException;

    /**
     * 从Minio中获取图片
     * @param stream 输出流
     * @param image 图片路径
     * @throws Exception 异常
     */
    void fetchImageFromMinio(OutputStream stream, String image) throws Exception;
}
