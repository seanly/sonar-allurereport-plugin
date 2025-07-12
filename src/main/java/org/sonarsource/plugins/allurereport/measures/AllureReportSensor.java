package org.sonarsource.plugins.allurereport.measures;

import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import org.sonar.api.scanner.sensor.ProjectSensor;
import org.sonar.api.utils.log.Profiler;
import org.sonarsource.plugins.allurereport.settings.AllureReportSettings;
import org.sonarsource.plugins.allurereport.uitls.AllureReportUtils;
import org.sonarsource.plugins.allurereport.uitls.MinIOUploader;

/**
 * Sensor that processes Allure test reports during SonarQube analysis.
 * Extracts test metrics and stores report files for web display.
 */
public class AllureReportSensor implements Sensor {

    private static final Logger LOGGER = Loggers.get(AllureReportSensor.class);
    private static final String ALLURE_RESULTS_DIR = "allure-results";
    private static final String ALLURE_REPORT_DIR = "allure-report";
    private static final String ALLURE_STORAGE_DIR = "allure-reports";

    private static final String SENSOR_NAME = "AllureReport";

    @Override
    public String toString() {
        return SENSOR_NAME;
    }

    @Override
    public void describe(SensorDescriptor sensorDescriptor) {
        sensorDescriptor.name(SENSOR_NAME)
                .onlyOnLanguage("java")
                .createIssuesForRuleRepositories("allure");
    }

    @Override
    public void execute(SensorContext sensorContext) {
        Profiler profiler = Profiler.create(LOGGER);
        profiler.startInfo("Process Allure report");
        if (AllureReportUtils.skipPlugin(sensorContext.config())) {
            LOGGER.info("Allure Report skipped");
        } else {
            uploadHTMLReport(sensorContext);
        }
        profiler.stopInfo();
    }

    private void uploadHTMLReport(SensorContext sensorContext) {
        try {
            // 检查是否启用MinIO上传
            boolean minioUploadEnabled = sensorContext.config()
                    .getBoolean(AllureReportSettings.MINIO_UPLOAD_ENABLED_KEY)
                    .orElse(AllureReportSettings.MINIO_UPLOAD_ENABLED_DEFAULT_VALUE);
            
            if (!minioUploadEnabled) {
                LOGGER.info("MinIO上传功能已禁用，跳过上传");
                return;
            }
            
            // 获取HTML报告路径
            String htmlReportPath = sensorContext.config()
                    .get(AllureReportSettings.HTML_REPORT_PATH_KEY)
                    .orElse(AllureReportSettings.HTML_REPORT_PATH_DEFAULT_VALUE);
            
            // 获取项目信息
            String projectKey = sensorContext.project().key();
            String projectVersion = sensorContext.config().get("sonar.projectVersion").orElse("");
            String branchName = sensorContext.config().get("sonar.branch.name").orElse("main");
            
            if (projectVersion.isEmpty()) {
                LOGGER.warn("项目版本未设置，无法上传到MinIO");
                return;
            }
            
            LOGGER.info("准备上传HTML报告到MinIO - 项目: {}, 版本: {}, 分支: {}, 路径: {}", 
                    projectKey, projectVersion, branchName, htmlReportPath);
            
            // 创建MinIO上传器并执行上传
            MinIOUploader uploader = new MinIOUploader(sensorContext.config());
            boolean uploadSuccess = uploader.uploadHtmlReport(htmlReportPath, projectKey, projectVersion, branchName);
            
            if (uploadSuccess) {
                LOGGER.info("HTML报告上传到MinIO成功");
                
                String allureReportUrl;
                // 生成nginx代理URL
                String bucket = sensorContext.config()
                        .get(AllureReportSettings.MINIO_BUCKET_KEY)
                        .orElse(AllureReportSettings.MINIO_BUCKET_DEFAULT_VALUE);
                
                // 获取站点地址配置
                String siteAddress = sensorContext.config()
                        .get(AllureReportSettings.SITE_ADDRESS_KEY)
                        .orElse(AllureReportSettings.SITE_ADDRESS_DEFAULT_VALUE);
                
                // 生成基础路径（不包含minio）
                String basePath = String.format("/%s/%s/%s/site/index.html", 
                        bucket, projectKey, branchName);
                
                // 如果配置了站点地址，则使用完整URL
                if (!siteAddress.isEmpty()) {
                    allureReportUrl = siteAddress + basePath;
                    LOGGER.info("使用站点地址URL访问内容: {}", allureReportUrl);
                } else {
                    // 没有配置站点地址时，添加 /minio 前缀用于nginx代理
                    allureReportUrl = "/minio" + basePath;
                    LOGGER.info("使用nginx代理URL访问内容: {}", allureReportUrl);
                }
                
                // 保存到metrics
                sensorContext.<String>newMeasure()
                        .forMetric(AllureReportMetrics.ALLURE_REPORT)
                        .on(sensorContext.project())
                        .withValue(allureReportUrl)
                        .save();
                
                LOGGER.info("Allure报告URL已保存到metrics: {}", allureReportUrl);
            } else {
                LOGGER.error("HTML报告上传到MinIO失败");
            }
            
        } catch (Exception e) {
            LOGGER.error("上传HTML报告过程中发生错误", e);
        }
    }

} 