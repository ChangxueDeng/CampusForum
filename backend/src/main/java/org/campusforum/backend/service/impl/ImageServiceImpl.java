package org.campusforum.backend.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.minio.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.campusforum.backend.entity.dto.Account;
import org.campusforum.backend.entity.dto.ImageStore;
import org.campusforum.backend.mapper.AccountMapper;
import org.campusforum.backend.mapper.ImageStoreMapper;
import org.campusforum.backend.service.ImageService;
import org.campusforum.backend.utils.Const;
import org.campusforum.backend.utils.FlowUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * @author ChangxueDeng
 * @date 2024/04/05
 */
@Service
@Slf4j
public class ImageServiceImpl extends ServiceImpl<ImageStoreMapper, ImageStore> implements ImageService {
    @Value("${minio.bucket}")
    private String bucket;
    @Resource
    private MinioClient minioClient;
    @Resource
    AccountMapper mapper;
    @Resource
    FlowUtils flowUtils;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    @Override
    public String uploadCache(MultipartFile file, int id) throws IOException {
        String key = Const.FORUM_IMAGE_COUNT + id;
        if (flowUtils.limitCountPeriod(key, 3600, 20)) {
            return null;
        }
        Date date = new Date();
        //生成UUID
        String imageName = UUID.randomUUID().toString().replace("-", "");
        //文件路径
        imageName = "/cache/" + dateFormat.format(date) + "/" + imageName;
        //进行上传
        PutObjectArgs args = PutObjectArgs.builder()
                .bucket(bucket)
                .object(imageName)
                .stream(file.getInputStream(), file.getSize(), -1)
                .build();
        try {
            minioClient.putObject(args);
            ImageStore imageStore = new ImageStore();
            imageStore.setName(imageName);
            imageStore.setUid(id);
            imageStore.setTime(date);
            if (this.save(imageStore)) {
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
            //获取旧头像
            String oldAvatar = mapper.selectById(id).getAvatar();
            if (mapper.update(null, Wrappers.<Account>update().eq("id", id).set("avatar", imageName)) > 0) {
                //进行删除旧头像
                this.deleteOldAvatar(oldAvatar);
                return imageName;
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("图片上传失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 删除旧头像文件
     * <p>将旧头像从Minio存储桶中删除</p>
     * @param avatar 图像路径
     * @throws Exception 异常
     */
    private void deleteOldAvatar(String avatar) throws Exception{
        if (avatar == null || avatar.isEmpty()) {
            return;
        }
        RemoveObjectArgs args = RemoveObjectArgs.builder().bucket( bucket).object(avatar).build();
        minioClient.removeObject(args);
    }

    @Override
    public void fetchImageFromMinio(OutputStream stream, String image) throws Exception {
        GetObjectArgs args = GetObjectArgs.builder().bucket(bucket).object(image).build();
        GetObjectResponse response = minioClient.getObject(args);
        IOUtils.copy(response, stream);
    }

    @Override
    public void deleteCache(String name) throws Exception{
        //获取得到对于minio的名称
        String key = name.substring(name.indexOf("/cache"));
        //查询数据库并进行删除
        if (this.remove(Wrappers.<ImageStore>query().eq("name", key))) {
            RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder().bucket(bucket).object(key).build();
            minioClient.removeObject(removeObjectArgs);
        }
    }
}
