package org.sonarsource.plugins.allurereport.measures;

import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;
import org.sonar.api.scanner.ScannerSide;

import java.util.List;

@ScannerSide
public final class AllureReportMetrics implements Metrics {

    private static final String DOMAIN = "Allure-Report";

    private static final String ALLURE_REPORT_KEY = "allure-report";

    public static final Metric<String> ALLURE_REPORT = new Metric.Builder(ALLURE_REPORT_KEY, "Allure Report", Metric.ValueType.STRING)
            .setDescription("Allure Report HTML")
            .setQualitative(Boolean.FALSE)
            .setDomain(AllureReportMetrics.DOMAIN)
            .setHidden(false)
            .setDeleteHistoricalData(true)
            .create();

    @Override
    public List<Metric> getMetrics() {
        return List.of(
                AllureReportMetrics.ALLURE_REPORT
        );
    }
}
