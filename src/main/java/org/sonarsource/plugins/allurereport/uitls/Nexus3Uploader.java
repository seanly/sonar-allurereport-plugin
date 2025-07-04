package org.sonarsource.plugins.allurereport.uitls;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.sonar.api.config.Configuration;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonarsource.plugins.allurereport.settings.AllureReportSettings;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * 用于将Allure HTML报告上传到Nexus3仓库的工具类
 */
public class Nexus3Uploader {
    
    private static final Logger LOGGER = Loggers.get(Nexus3Uploader.class);
    
    private final Configuration config;
    
    public Nexus3Uploader(Configuration config) {
        this.config = config;
    }
    
    /**
     * 上传HTML报告目录到Nexus3仓库
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
            
            String nexusUrl = config.get(AllureReportSettings.NEXUS_URL_KEY)
                    .orElse(AllureReportSettings.NEXUS_URL_DEFAULT_VALUE);
            String username = config.get(AllureReportSettings.NEXUS_USERNAME_KEY)
                    .orElse(AllureReportSettings.NEXUS_USERNAME_DEFAULT_VALUE);
            String password = config.get(AllureReportSettings.NEXUS_PASSWORD_KEY)
                    .orElse(AllureReportSettings.NEXUS_PASSWORD_DEFAULT_VALUE);
            String repository = config.get(AllureReportSettings.NEXUS_REPOSITORY_KEY)
                    .orElse(AllureReportSettings.NEXUS_REPOSITORY_DEFAULT_VALUE);
            
            if (username.isEmpty() || password.isEmpty()) {
                LOGGER.warn("Nexus3用户名或密码未配置，跳过上传");
                return false;
            }
            
            LOGGER.info("开始上传HTML报告到Nexus3: {}", nexusUrl);
            
            // 构建上传URL
            String uploadUrl = buildUploadUrl(nexusUrl, repository, projectKey, projectVersion, branchName);
            
            // 上传目录中的所有文件
            return uploadDirectory(reportDir, uploadUrl, username, password);
            
        } catch (Exception e) {
            LOGGER.error("上传HTML报告到Nexus3失败", e);
            return false;
        }
    }
    
    /**
     * 构建Nexus3上传URL，支持分支目录
     */
    private String buildUploadUrl(String nexusUrl, String repository, String projectKey, String projectVersion, String branchName) {
        // 移除URL末尾的斜杠
        String baseUrl = nexusUrl.endsWith("/") ? nexusUrl.substring(0, nexusUrl.length() - 1) : nexusUrl;
        String sb = baseUrl + "/repository/" + repository + "/" +
                projectKey + "/" +
                branchName + "/" +
                "site/";
        return sb;
    }
    
    /**
     * 递归上传目录中的所有文件
     */
    private boolean uploadDirectory(Path directory, String baseUploadUrl, String username, String password) {
        try (Stream<Path> paths = Files.walk(directory)) {
            return paths.filter(Files::isRegularFile)
                    .allMatch(file -> uploadFile(file, directory, baseUploadUrl, username, password));
        } catch (IOException e) {
            LOGGER.error("遍历目录失败: {}", directory, e);
            return false;
        }
    }
    
    /**
     * 上传单个文件到Nexus3
     */
    private boolean uploadFile(Path file, Path baseDir, String baseUploadUrl, String username, String password) {
        try {
            // 计算相对路径
            String relativePath = baseDir.relativize(file).toString();
            String uploadUrl = baseUploadUrl + relativePath;
            
            LOGGER.debug("上传文件: {} -> {}", file, uploadUrl);
            
            // 创建HTTP客户端
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpPut httpPut = new HttpPut(uploadUrl);
                
                // 设置认证
                String auth = username + ":" + password;
                String encodedAuth = java.util.Base64.getEncoder().encodeToString(auth.getBytes());
                httpPut.setHeader("Authorization", "Basic " + encodedAuth);
                
                // 设置Content-Type
                String contentType = getContentType(file);
                httpPut.setHeader("Content-Type", contentType);
                
                // 上传文件内容
                try (FileInputStream fis = new FileInputStream(file.toFile())) {
                    byte[] fileContent = Files.readAllBytes(file);
                    httpPut.setEntity(new org.apache.http.entity.ByteArrayEntity(fileContent));
                    
                    // 执行请求
                    try (CloseableHttpResponse response = httpClient.execute(httpPut)) {
                        int statusCode = response.getStatusLine().getStatusCode();
                        if (statusCode >= 200 && statusCode < 300) {
                            LOGGER.debug("文件上传成功: {}", relativePath);
                            return true;
                        } else {
                            String responseBody = EntityUtils.toString(response.getEntity());
                            LOGGER.error("文件上传失败: {} (状态码: {}, 响应: {})", 
                                    relativePath, statusCode, responseBody);
                            return false;
                        }
                    }
                }
            }
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