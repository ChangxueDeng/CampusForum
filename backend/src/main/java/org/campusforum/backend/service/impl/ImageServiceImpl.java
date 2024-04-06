package org.campusforum.backend.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.campusforum.backend.entity.dto.Account;
import org.campusforum.backend.mapper.AccountMapper;
import org.campusforum.backend.service.ImageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 * @author ChangxueDeng
 * @date 2024/04/05
 */
@Service
@Slf4j
public class ImageServiceImpl implements ImageService {
    @Value("${minio.bucket}")
    private String bucket;
    @Resource
    private MinioClient minioClient;
    @Resource
    AccountMapper mapper;
    @Override
    public String uploadAvatar(MultipartFile file, int id) throws IOException {
        //生成UUID
        String imageName = UUID.randomUUID().toString().replace("-", "");
        //头像路径
        imageName = "/avatar/" + imageName;
        //进行上传
        PutObjectArgs args = PutObjectArgs.builder()
                .bucket(bucket)
                .object(imageName)
                .stream(file.getInputStream(), file.getSize(), -1)
                .build();
        try {
            minioClient.putObject(args);
            if (mapper.update(null, Wrappers.<Account>update().eq("id", id).set("avatar", imageName)) > 0) {
                return imageName;
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("图片上传失败: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public void fetchImageFromMinio(OutputStream stream, String image) throws Exception {
        GetObjectArgs args = GetObjectArgs.builder().bucket(bucket).object(image).build();
        GetObjectResponse response = minioClient.getObject(args);
        IOUtils.copy(response, stream);
    }
}
