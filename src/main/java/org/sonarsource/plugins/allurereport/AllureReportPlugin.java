package org.sonarsource.plugins.allurereport;

import org.sonar.api.Plugin;
import org.sonarsource.plugins.allurereport.measures.AllureReportMetrics;
import org.sonarsource.plugins.allurereport.measures.AllureReportSensor;
import org.sonarsource.plugins.allurereport.settings.AllureReportSettings;
import org.sonarsource.plugins.allurereport.web.AllureReportPage;


import java.util.Arrays;

public final class AllureReportPlugin implements Plugin {

    @Override
    public void define(Context context) {
        context.addExtensions(Arrays.asList(
                AllureReportSensor.class,
                AllureReportMetrics.class,
                AllureReportPage.class));
        context.addExtensions(AllureReportSettings.getPropertyDefinitions());
    }
}
