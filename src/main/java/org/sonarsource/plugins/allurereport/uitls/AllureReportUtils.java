package org.sonarsource.plugins.allurereport.uitls;

import org.sonar.api.config.Configuration;

public final class AllureReportUtils {

    private AllureReportUtils() {
    }

    public static boolean skipPlugin(Configuration config) {
        return config.getBoolean(AllureReportConstants.SKIP_PROPERTY).orElse(AllureReportConstants.SKIP_PROPERTY_DEFAULT);
    }

}
