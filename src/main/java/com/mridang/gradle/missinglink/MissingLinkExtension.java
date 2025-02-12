package com.mridang.gradle.missinglink;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Collections;
import javax.inject.Inject;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;

/**
 * Configuration extension for the MissingLink Gradle plugin.
 *
 * <p>This extension allows users to configure how MissingLink operates, including skipping checks,
 * handling conflicts, and configuring reports.
 */
public abstract class MissingLinkExtension {

  private final Property<Boolean> skip;
  private final Property<Boolean> failOnConflicts;
  private final ListProperty<String> includeCategories;
  private final ListProperty<String> excludeDependencies;
  private final MissingLinkReports reports;

  /**
   * Constructs the MissingLink extension, initializing its properties.
   *
   * @param project The Gradle project this extension is associated with.
   * @param objectFactory The Gradle {@link ObjectFactory} for creating properties.
   */
  @SuppressWarnings("InjectOnConstructorOfAbstractClass")
  @SuppressFBWarnings("CT_CONSTRUCTOR_THROW")
  @Inject
  public MissingLinkExtension(Project project, ObjectFactory objectFactory) {
    this.skip = objectFactory.property(Boolean.class).convention(false);
    this.failOnConflicts = objectFactory.property(Boolean.class).convention(true);
    this.includeCategories =
        objectFactory.listProperty(String.class).convention(Collections.emptyList());
    this.excludeDependencies =
        objectFactory.listProperty(String.class).convention(Collections.emptyList());
    this.reports = objectFactory.newInstance(MissingLinkReportsImpl.class, project, objectFactory);
  }

  /**
   * Determines whether the MissingLink check should be skipped.
   *
   * @return A {@link Property} indicating whether to skip the check.
   */
  @SuppressWarnings("unused")
  @SuppressFBWarnings("EI_EXPOSE_REP")
  public Property<Boolean> getSkip() {
    return skip;
  }

  /**
   * Determines whether the build should fail if conflicts are detected.
   *
   * @return A {@link Property} indicating whether to fail on conflicts.
   */
  @SuppressFBWarnings("EI_EXPOSE_REP")
  public Property<Boolean> getFailOnConflicts() {
    return failOnConflicts;
  }

  /**
   * Gets the list of categories to include in the MissingLink check.
   *
   * @return A {@link ListProperty} containing the included categories.
   */
  @SuppressWarnings("unused")
  public ListProperty<String> getIncludeCategories() {
    return includeCategories;
  }

  /**
   * Gets the list of dependencies to exclude from the MissingLink check.
   *
   * @return A {@link ListProperty} containing the excluded dependencies.
   */
  public ListProperty<String> getExcludeDependencies() {
    return excludeDependencies;
  }

  /**
   * Adds a dependency to the exclusion list.
   *
   * @param dependency The dependency to exclude (format: "group:name").
   */
  @SuppressWarnings("unused")
  public void excludeDependency(String dependency) {
    this.excludeDependencies.add(dependency);
  }

  /**
   * Returns the reports configuration for the MissingLink plugin.
   *
   * @return The {@link MissingLinkReports} instance containing report settings.
   */
  public MissingLinkReports getReports() {
    return reports;
  }

  /**
   * Configures the reports using a DSL block.
   *
   * @param configureAction The action to configure the reports.
   */
  @SuppressWarnings("unused")
  public void reports(Action<? super MissingLinkReports> configureAction) {
    configureAction.execute(reports);
  }
}
