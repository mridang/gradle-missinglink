package com.mridang.gradle.missinglink;

import java.util.List;
import javax.inject.Inject;
import org.gradle.api.Project;
import org.gradle.api.file.Directory;
import org.gradle.api.file.RegularFile;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Provider;
import org.gradle.api.reporting.CustomizableHtmlReport;
import org.gradle.api.reporting.ReportingExtension;
import org.gradle.api.reporting.SingleFileReport;
import org.gradle.api.reporting.internal.CustomizableHtmlReportImpl;
import org.gradle.api.reporting.internal.DefaultReportContainer;
import org.gradle.api.reporting.internal.DefaultSingleFileReport;
import org.gradle.api.reporting.internal.DelegatingReportContainer;

public class MissingLinkReportsImpl extends DelegatingReportContainer<SingleFileReport>
    implements MissingLinkReports {

  @Inject
  public MissingLinkReportsImpl(Project project, ObjectFactory objectFactory) {
    super(
        DefaultReportContainer.create(
            objectFactory,
            SingleFileReport.class,
            factory -> {
              CustomizableHtmlReportImpl htmlReport =
                  factory.instantiateReport(CustomizableHtmlReportImpl.class, "html", project);
              DefaultSingleFileReport xmlReport =
                  factory.instantiateReport(DefaultSingleFileReport.class, "xml", project);
              DefaultSingleFileReport sarifReport =
                  factory.instantiateReport(DefaultSingleFileReport.class, "sarif", project);

              // Get reports directory as Provider<Directory>
              ReportingExtension reporting =
                  project.getExtensions().getByType(ReportingExtension.class);
              Provider<Directory> reportsDir = reporting.getBaseDirectory().dir("missinglink");

              // Explicitly convert Provider<Directory> to Provider<RegularFile>
              Provider<RegularFile> htmlFile = reportsDir.map(dir -> dir.file("missinglink.html"));
              Provider<RegularFile> xmlFile = reportsDir.map(dir -> dir.file("missinglink.xml"));
              Provider<RegularFile> sarifFile =
                  reportsDir.map(dir -> dir.file("missinglink.sarif"));

              // Assign output locations
              htmlReport.getOutputLocation().set(htmlFile.get().getAsFile());
              xmlReport.getOutputLocation().set(xmlFile.get().getAsFile());
              sarifReport.getOutputLocation().set(sarifFile.get().getAsFile());

              // ✅ Enable reports by default
              htmlReport.getRequired().set(true);
              xmlReport.getRequired().set(true);
              sarifReport.getRequired().set(true);

              return List.of(htmlReport, xmlReport, sarifReport);
            }));
  }

  @Override
  public CustomizableHtmlReport getHtml() {
    return withType(CustomizableHtmlReport.class).getByName("html");
  }

  @Override
  public SingleFileReport getXml() {
    return getByName("xml");
  }

  @Override
  public SingleFileReport getSarif() {
    return getByName("sarif");
  }
}
