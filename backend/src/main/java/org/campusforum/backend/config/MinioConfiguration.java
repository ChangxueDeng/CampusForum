package org.campusforum.backend.config;

import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Minio配置类
 * <P>用于配置Minio Client</p>
 * @author ChangxueDeng
 * @date 2024/04/05
 */
@Slf4j
@Configuration
public class MinioConfiguration {
    @Value("${minio.endpoint}")
    String endpoint;
    @Value("${minio.accessKey}")
    String accessKey;
    @Value("${minio.secretKey}")
    String secretKey;

    @Bean
    public MinioClient minioClient() {
        log.info("Init MinioClient...");
        return MinioClient
                .builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}
