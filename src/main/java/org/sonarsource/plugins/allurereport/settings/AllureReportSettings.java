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
    public static final String NEXUS_URL_KEY = "sonar.allure.nexus.url";
    public static final String NEXUS_URL_DEFAULT_VALUE = "https://nexus.example.com";

    public static final String NEXUS_USERNAME_KEY = "sonar.allure.nexus.username";
    public static final String NEXUS_USERNAME_DEFAULT_VALUE = "";

    public static final String NEXUS_PASSWORD_KEY = "sonar.allure.nexus.password";
    public static final String NEXUS_PASSWORD_DEFAULT_VALUE = "";

    public static final String NEXUS_REPOSITORY_KEY = "sonar.allure.nexus.repository";
    public static final String NEXUS_REPOSITORY_DEFAULT_VALUE = "allure-reports";

    public static final String NEXUS_UPLOAD_ENABLED_KEY = "sonar.allure.nexus.upload.enabled";
    public static final Boolean NEXUS_UPLOAD_ENABLED_DEFAULT_VALUE = Boolean.TRUE;

    public static final String HTML_REPORT_PATH_KEY = "sonar.allureReport.htmlReportPath";
    public static final String HTML_REPORT_PATH_DEFAULT_VALUE = "target/site/allure-maven-plugin";

    public static final String JSON_REPORT_PATH_KEY = "sonar.allureReport.jsonReportPath";
    public static final String JSON_REPORT_PATH_DEFAULT_VALUE = "target/allure-results";

    public static final String CATEGORY = "Allure Report";

    private AllureReportSettings() {}

    public static List<PropertyDefinition> getPropertyDefinitions() {
        return asList(
                PropertyDefinition.builder(NEXUS_URL_KEY)
                        .name("Nexus3 URL")
                        .description("Nexus3 服务器地址")
                        .defaultValue(NEXUS_URL_DEFAULT_VALUE)
                        .category(CATEGORY)
                        .subCategory(AllureReportConstants.SUB_CATEGORY_NEXUS)
                        .onQualifiers(Qualifiers.PROJECT)
                        .build(),
                PropertyDefinition.builder(NEXUS_USERNAME_KEY)
                        .name("Nexus3 用户名")
                        .description("用于上传 Allure 报告的 Nexus3 用户名")
                        .defaultValue(NEXUS_USERNAME_DEFAULT_VALUE)
                        .category(CATEGORY)
                        .subCategory(AllureReportConstants.SUB_CATEGORY_NEXUS)
                        .onQualifiers(Qualifiers.PROJECT)
                        .build(),
                PropertyDefinition.builder(NEXUS_PASSWORD_KEY)
                        .name("Nexus3 密码")
                        .description("用于上传 Allure 报告的 Nexus3 密码")
                        .defaultValue(NEXUS_PASSWORD_DEFAULT_VALUE)
                        .category(CATEGORY)
                        .subCategory(AllureReportConstants.SUB_CATEGORY_NEXUS)
                        .type(PropertyType.PASSWORD)
                        .onQualifiers(Qualifiers.PROJECT)
                        .build(),
                PropertyDefinition.builder(NEXUS_REPOSITORY_KEY)
                        .name("Nexus3 仓库名")
                        .description("Nexus3 仓库名称，如 maven-releases 或 maven-snapshots")
                        .defaultValue(NEXUS_REPOSITORY_DEFAULT_VALUE)
                        .category(CATEGORY)
                        .subCategory(AllureReportConstants.SUB_CATEGORY_NEXUS)
                        .onQualifiers(Qualifiers.PROJECT)
                        .build(),
                PropertyDefinition.builder(NEXUS_UPLOAD_ENABLED_KEY)
                        .name("Nexus3 上传启用")
                        .description("是否启用 Nexus3 上传")
                        .type(PropertyType.BOOLEAN)
                        .defaultValue(String.valueOf(NEXUS_UPLOAD_ENABLED_DEFAULT_VALUE))
                        .category(CATEGORY)
                        .subCategory(AllureReportConstants.SUB_CATEGORY_NEXUS)
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