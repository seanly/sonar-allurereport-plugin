package org.sonarsource.plugins.allurereport.web;

import org.sonar.api.web.page.Context;
import org.sonar.api.web.page.Page;
import org.sonar.api.web.page.PageDefinition;

public class AllureReportPage implements PageDefinition {

    @Override
    public void define(Context context) {
        context.addPage(
                Page.builder("allurereport/report_page")
                        .setScope(Page.Scope.COMPONENT)
                        .setComponentQualifiers(Page.Qualifier.PROJECT, Page.Qualifier.MODULE)
                        .setName("Allure-Report")
                        .setAdmin(false).build());
    }
}
