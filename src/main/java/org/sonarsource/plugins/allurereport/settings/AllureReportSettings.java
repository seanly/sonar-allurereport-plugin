package org.sonarsource.plugins.allurereport.settings;

import org.sonar.api.PropertyType;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.resources.Qualifiers;
import org.sonarsource.plugins.allurereport.uitls.AllureReportConstants;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * Allure 报告上传与集成相关的全局配置。
 */
public class AllureReportSettings {
    public static final String MINIO_ENDPOINT_KEY = "sonar.allure.minio.endpoint";
    public static final String MINIO_ENDPOINT_DEFAULT_VALUE = "http://localhost:9000";

    public static final String MINIO_ACCESS_KEY_KEY = "sonar.allure.minio.accessKey";
    public static final String MINIO_ACCESS_KEY_DEFAULT_VALUE = "";

    public static final String MINIO_SECRET_KEY_KEY = "sonar.allure.minio.secretKey";
    public static final String MINIO_SECRET_KEY_DEFAULT_VALUE = "";

    public static final String MINIO_BUCKET_KEY = "sonar.allure.minio.bucket";
    public static final String MINIO_BUCKET_DEFAULT_VALUE = "allure-reports";

    public static final String MINIO_USE_SSL_KEY = "sonar.allure.minio.useSSL";
    public static final Boolean MINIO_USE_SSL_DEFAULT_VALUE = Boolean.FALSE;

    public static final String MINIO_UPLOAD_ENABLED_KEY = "sonar.allure.minio.upload.enabled";
    public static final Boolean MINIO_UPLOAD_ENABLED_DEFAULT_VALUE = Boolean.TRUE;

    public static final String SITE_ADDRESS_KEY = "sonar.allure.site.address";
    public static final String SITE_ADDRESS_DEFAULT_VALUE = "";

    public static final String HTML_REPORT_PATH_KEY = "sonar.allureReport.htmlReportPath";
    public static final String HTML_REPORT_PATH_DEFAULT_VALUE = "target/site/allure-maven-plugin";

    public static final String JSON_REPORT_PATH_KEY = "sonar.allureReport.jsonReportPath";
    public static final String JSON_REPORT_PATH_DEFAULT_VALUE = "target/allure-results";

    public static final String CATEGORY = "Allure Report";

    private AllureReportSettings() {}

    public static List<PropertyDefinition> getPropertyDefinitions() {
        return asList(
                PropertyDefinition.builder(MINIO_ENDPOINT_KEY)
                        .name("MinIO Endpoint")
                        .description("MinIO 服务器地址")
                        .defaultValue(MINIO_ENDPOINT_DEFAULT_VALUE)
                        .category(CATEGORY)
                        .subCategory(AllureReportConstants.SUB_CATEGORY_MINIO)
                        .onQualifiers(Qualifiers.PROJECT)
                        .build(),
                PropertyDefinition.builder(MINIO_ACCESS_KEY_KEY)
                        .name("MinIO Access Key")
                        .description("用于上传 Allure 报告的 MinIO 访问密钥")
                        .defaultValue(MINIO_ACCESS_KEY_DEFAULT_VALUE)
                        .category(CATEGORY)
                        .subCategory(AllureReportConstants.SUB_CATEGORY_MINIO)
                        .onQualifiers(Qualifiers.PROJECT)
                        .build(),
                PropertyDefinition.builder(MINIO_SECRET_KEY_KEY)
                        .name("MinIO Secret Key")
                        .description("用于上传 Allure 报告的 MinIO 秘密密钥")
                        .defaultValue(MINIO_SECRET_KEY_DEFAULT_VALUE)
                        .category(CATEGORY)
                        .subCategory(AllureReportConstants.SUB_CATEGORY_MINIO)
                        .type(PropertyType.PASSWORD)
                        .onQualifiers(Qualifiers.PROJECT)
                        .build(),
                PropertyDefinition.builder(MINIO_BUCKET_KEY)
                        .name("MinIO Bucket")
                        .description("MinIO 存储桶名称")
                        .defaultValue(MINIO_BUCKET_DEFAULT_VALUE)
                        .category(CATEGORY)
                        .subCategory(AllureReportConstants.SUB_CATEGORY_MINIO)
                        .onQualifiers(Qualifiers.PROJECT)
                        .build(),
                PropertyDefinition.builder(MINIO_USE_SSL_KEY)
                        .name("MinIO 使用SSL")
                        .description("是否使用SSL连接MinIO")
                        .type(PropertyType.BOOLEAN)
                        .defaultValue(String.valueOf(MINIO_USE_SSL_DEFAULT_VALUE))
                        .category(CATEGORY)
                        .subCategory(AllureReportConstants.SUB_CATEGORY_MINIO)
                        .onQualifiers(Qualifiers.PROJECT)
                        .build(),
                PropertyDefinition.builder(MINIO_UPLOAD_ENABLED_KEY)
                        .name("MinIO 上传启用")
                        .description("是否启用 MinIO 上传")
                        .type(PropertyType.BOOLEAN)
                        .defaultValue(String.valueOf(MINIO_UPLOAD_ENABLED_DEFAULT_VALUE))
                        .category(CATEGORY)
                        .subCategory(AllureReportConstants.SUB_CATEGORY_MINIO)
                        .onQualifiers(Qualifiers.PROJECT)
                        .build(),
                PropertyDefinition.builder(SITE_ADDRESS_KEY)
                        .name("站点地址")
                        .description("用于替换 /minio 路径的站点地址，例如: https://example.com")
                        .defaultValue(SITE_ADDRESS_DEFAULT_VALUE)
                        .category(CATEGORY)
                        .subCategory(AllureReportConstants.SUB_CATEGORY_MINIO)
                        .onQualifiers(Qualifiers.PROJECT)
                        .build(),

                PropertyDefinition.builder(HTML_REPORT_PATH_KEY)
                        .name("Allure Report HTML report path")
                        .description("path to the allure report html files")
                        .defaultValue(HTML_REPORT_PATH_DEFAULT_VALUE)
                        .category(CATEGORY)
                        .subCategory(AllureReportConstants.SUB_CATEGORY_PATHS)
                        .onQualifiers(Qualifiers.PROJECT)
                        .build(),
                PropertyDefinition.builder(JSON_REPORT_PATH_KEY)
                        .name("Allure Report JSON report path")
                        .description("path to the 'allure-results' directory")
                        .defaultValue(JSON_REPORT_PATH_DEFAULT_VALUE)
                        .category(CATEGORY)
                        .subCategory(AllureReportConstants.SUB_CATEGORY_PATHS)
                        .onQualifiers(Qualifiers.PROJECT)
                        .build()
        );
    }
} 