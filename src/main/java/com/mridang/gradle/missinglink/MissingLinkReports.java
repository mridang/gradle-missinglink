package com.mridang.gradle.missinglink;

import org.gradle.api.reporting.CustomizableHtmlReport;
import org.gradle.api.reporting.ReportContainer;
import org.gradle.api.reporting.SingleFileReport;
import org.gradle.api.tasks.Internal;

/** The reporting configuration for the {@link MissingLinkTask} task. */
public interface MissingLinkReports extends ReportContainer<SingleFileReport> {

  /**
   * The missinglink HTML report.
   *
   * <p>This report IS enabled by default.
   *
   * @return The missinglink HTML report
   */
  @Internal
  CustomizableHtmlReport getHtml();

  /**
   * The missinglink XML report
   *
   * <p>This report IS enabled by default.
   *
   * @return The missinglink XML report
   */
  @Internal
  SingleFileReport getXml();

  /**
   * The missinglink SARIF report
   *
   * <p>This report is NOT enabled by default.
   *
   * @return The missinglink SARIF report
   */
  @Internal
  SingleFileReport getSarif();
}
