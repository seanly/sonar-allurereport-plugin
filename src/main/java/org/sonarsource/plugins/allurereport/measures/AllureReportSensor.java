package org.sonarsource.plugins.allurereport.measures;

import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import org.sonar.api.scanner.sensor.ProjectSensor;
import org.sonar.api.utils.log.Profiler;
import org.sonarsource.plugins.allurereport.settings.AllureReportSettings;
import org.sonarsource.plugins.allurereport.uitls.AllureReportUtils;
import org.sonarsource.plugins.allurereport.uitls.Nexus3Uploader;

/**
 * Sensor that processes Allure test reports during SonarQube analysis.
 * Extracts test metrics and stores report files for web display.
 */
public class AllureReportSensor implements ProjectSensor {

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
        sensorDescriptor.name(SENSOR_NAME);
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
            // 检查是否启用Nexus3上传
            boolean nexusUploadEnabled = sensorContext.config()
                    .getBoolean(AllureReportSettings.NEXUS_UPLOAD_ENABLED_KEY)
                    .orElse(AllureReportSettings.NEXUS_UPLOAD_ENABLED_DEFAULT_VALUE);
            
            if (!nexusUploadEnabled) {
                LOGGER.info("Nexus3上传功能已禁用，跳过上传");
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
                LOGGER.warn("项目版本未设置，无法上传到Nexus3");
                return;
            }
            
            LOGGER.info("准备上传HTML报告到Nexus3 - 项目: {}, 版本: {}, 分支: {}, 路径: {}", 
                    projectKey, projectVersion, branchName, htmlReportPath);
            
            // 创建Nexus3上传器并执行上传
            Nexus3Uploader uploader = new Nexus3Uploader(sensorContext.config());
            boolean uploadSuccess = uploader.uploadHtmlReport(htmlReportPath, projectKey, projectVersion, branchName);
            
            if (uploadSuccess) {
                LOGGER.info("HTML报告上传到Nexus3成功");
                
                // 生成Nexus3报告URL并保存到metrics
                String nexusUrl = sensorContext.config()
                        .get(AllureReportSettings.NEXUS_URL_KEY)
                        .orElse(AllureReportSettings.NEXUS_URL_DEFAULT_VALUE);
                String repository = sensorContext.config()
                        .get(AllureReportSettings.NEXUS_REPOSITORY_KEY)
                        .orElse(AllureReportSettings.NEXUS_REPOSITORY_DEFAULT_VALUE);
                
                // 构建报告首页URL
                String baseUrl = nexusUrl.endsWith("/") ? nexusUrl.substring(0, nexusUrl.length() - 1) : nexusUrl;
                String allureReportUrl = String.format("%s/repository/%s/%s/%s/site/index.html", 
                        baseUrl, repository, projectKey, branchName);
                
                // 保存到metrics
                sensorContext.<String>newMeasure()
                        .forMetric(AllureReportMetrics.ALLURE_REPORT)
                        .on(sensorContext.project())
                        .withValue(allureReportUrl)
                        .save();
                
                LOGGER.info("Allure报告URL已保存到metrics: {}", allureReportUrl);
            } else {
                LOGGER.error("HTML报告上传到Nexus3失败");
            }
            
        } catch (Exception e) {
            LOGGER.error("上传HTML报告过程中发生错误", e);
        }
    }

} 