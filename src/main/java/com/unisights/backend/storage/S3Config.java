package com.unisights.backend.storage;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean; import org.springframework.context.annotation.Configuration;
@Configuration
public class S3Config {
    @Bean MinioClient minio(
            @Value("${storage.s3.endpoint}") String endpoint,
            @Value("${storage.s3.accessKey}") String ak,
            @Value("${storage.s3.secretKey}") String sk
    ){
        return MinioClient.builder().endpoint(endpoint).credentials(ak, sk).build();
    }
}
