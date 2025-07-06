package org.sonarsource.plugins.allurereport.uitls;

import io.minio.*;
import io.minio.errors.*;
import org.sonar.api.config.Configuration;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonarsource.plugins.allurereport.settings.AllureReportSettings;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * 用于将Allure HTML报告上传到MinIO存储的工具类
 */
public class MinIOUploader {
    
    private static final Logger LOGGER = Loggers.get(MinIOUploader.class);
    
    private final Configuration config;
    
    public MinIOUploader(Configuration config) {
        this.config = config;
    }
    
    /**
     * 上传HTML报告目录到MinIO存储
     * 
     * @param htmlReportPath HTML报告目录路径
     * @param projectKey 项目键
     * @param projectVersion 项目版本
     * @param branchName 分支名称
     * @return 是否上传成功
     */
    public boolean uploadHtmlReport(String htmlReportPath, String projectKey, String projectVersion, String branchName) {
        try {
            Path reportDir = Paths.get(htmlReportPath);
            if (!Files.exists(reportDir) || !Files.isDirectory(reportDir)) {
                LOGGER.warn("HTML报告目录不存在: {}", htmlReportPath);
                return false;
            }
            
            String endpoint = config.get(AllureReportSettings.MINIO_ENDPOINT_KEY)
                    .orElse(AllureReportSettings.MINIO_ENDPOINT_DEFAULT_VALUE);
            String accessKey = config.get(AllureReportSettings.MINIO_ACCESS_KEY_KEY)
                    .orElse(AllureReportSettings.MINIO_ACCESS_KEY_DEFAULT_VALUE);
            String secretKey = config.get(AllureReportSettings.MINIO_SECRET_KEY_KEY)
                    .orElse(AllureReportSettings.MINIO_SECRET_KEY_DEFAULT_VALUE);
            String bucket = config.get(AllureReportSettings.MINIO_BUCKET_KEY)
                    .orElse(AllureReportSettings.MINIO_BUCKET_DEFAULT_VALUE);
            boolean useSSL = config.getBoolean(AllureReportSettings.MINIO_USE_SSL_KEY)
                    .orElse(AllureReportSettings.MINIO_USE_SSL_DEFAULT_VALUE);
            
            if (accessKey.isEmpty() || secretKey.isEmpty()) {
                LOGGER.warn("MinIO访问密钥或秘密密钥未配置，跳过上传");
                return false;
            }
            
            LOGGER.info("开始上传HTML报告到MinIO: {}", endpoint);
            
            // 创建MinIO客户端
            MinioClient minioClient = MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();
            
            // 确保bucket存在
            ensureBucketExists(minioClient, bucket);
            
            // 构建对象前缀
            String objectPrefix = buildObjectPrefix(projectKey, projectVersion, branchName);
            
            // 上传目录中的所有文件
            return uploadDirectory(reportDir, minioClient, bucket, objectPrefix);
            
        } catch (Exception e) {
            LOGGER.error("上传HTML报告到MinIO失败", e);
            return false;
        }
    }
    
    /**
     * 确保MinIO bucket存在，如果不存在则创建
     */
    private void ensureBucketExists(MinioClient minioClient, String bucket) {
        try {
            boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!bucketExists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
                LOGGER.info("创建MinIO bucket: {}", bucket);
            }
        } catch (Exception e) {
            LOGGER.error("检查或创建MinIO bucket失败: {}", bucket, e);
            throw new RuntimeException("无法确保MinIO bucket存在", e);
        }
    }
    

    
    /**
     * 构建MinIO对象前缀，支持分支目录
     */
    private String buildObjectPrefix(String projectKey, String projectVersion, String branchName) {
        return String.format("%s/%s/site/", projectKey, branchName);
    }
    
    /**
     * 递归上传目录中的所有文件
     */
    private boolean uploadDirectory(Path directory, MinioClient minioClient, String bucket, String objectPrefix) {
        try (Stream<Path> paths = Files.walk(directory)) {
            return paths.filter(Files::isRegularFile)
                    .allMatch(file -> uploadFile(file, directory, minioClient, bucket, objectPrefix));
        } catch (IOException e) {
            LOGGER.error("遍历目录失败: {}", directory, e);
            return false;
        }
    }
    
    /**
     * 上传单个文件到MinIO
     */
    private boolean uploadFile(Path file, Path baseDir, MinioClient minioClient, String bucket, String objectPrefix) {
        try {
            // 计算相对路径
            String relativePath = baseDir.relativize(file).toString();
            String objectName = objectPrefix + relativePath;
            
            LOGGER.debug("上传文件: {} -> {}", file, objectName);
            
            // 获取文件内容类型
            String contentType = getContentType(file);
            
            // 上传文件
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .stream(new FileInputStream(file.toFile()), Files.size(file), -1)
                            .contentType(contentType)
                            .build()
            );
            
            LOGGER.debug("文件上传成功: {}", relativePath);
            return true;
            
        } catch (Exception e) {
            LOGGER.error("上传文件失败: {}", file, e);
            return false;
        }
    }
    
    /**
     * 根据文件扩展名获取Content-Type
     */
    private String getContentType(Path file) {
        String fileName = file.getFileName().toString().toLowerCase();
        if (fileName.endsWith(".html")) {
            return "text/html";
        } else if (fileName.endsWith(".css")) {
            return "text/css";
        } else if (fileName.endsWith(".js")) {
            return "application/javascript";
        } else if (fileName.endsWith(".png")) {
            return "image/png";
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileName.endsWith(".gif")) {
            return "image/gif";
        } else if (fileName.endsWith(".svg")) {
            return "image/svg+xml";
        } else if (fileName.endsWith(".json")) {
            return "application/json";
        } else if (fileName.endsWith(".xml")) {
            return "application/xml";
        } else {
            return "application/octet-stream";
        }
    }
} 