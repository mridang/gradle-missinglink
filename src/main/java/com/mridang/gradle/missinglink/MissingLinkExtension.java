package com.mridang.gradle.missinglink;

import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;

public abstract class MissingLinkExtension {

  private final Property<Boolean> skip;
  private final Property<Boolean> failOnConflicts;
  private final ListProperty<String> includeCategories;
  private final ListProperty<String> includeScopes;
  private final ListProperty<String> excludeDependencies;
  private final ListProperty<String> ignoreSourcePackages;
  private final ListProperty<String> targetSourcePackages;
  private final ListProperty<String> ignoreDestinationPackages;
  private final ListProperty<String> targetDestinationPackages;
  private final MissingLinkReports reports;

  @Inject
  public MissingLinkExtension(Project project, ObjectFactory objectFactory) {
    this.skip = objectFactory.property(Boolean.class).convention(false);
    this.failOnConflicts = objectFactory.property(Boolean.class).convention(false);
    this.includeCategories =
        objectFactory.listProperty(String.class).convention(Collections.emptyList());
    this.includeScopes =
        objectFactory.listProperty(String.class).convention(List.of("compile", "test"));
    this.excludeDependencies =
        objectFactory.listProperty(String.class).convention(Collections.emptyList());
    this.ignoreSourcePackages =
        objectFactory.listProperty(String.class).convention(Collections.emptyList());
    this.targetSourcePackages =
        objectFactory.listProperty(String.class).convention(Collections.emptyList());
    this.ignoreDestinationPackages =
        objectFactory.listProperty(String.class).convention(Collections.emptyList());
    this.targetDestinationPackages =
        objectFactory.listProperty(String.class).convention(Collections.emptyList());
    this.reports = objectFactory.newInstance(MissingLinkReportsImpl.class, project, objectFactory);
  }

  public Property<Boolean> getSkip() {
    return skip;
  }

  public Property<Boolean> getFailOnConflicts() {
    return failOnConflicts;
  }

  public ListProperty<String> getIncludeCategories() {
    return includeCategories;
  }

  public ListProperty<String> getIncludeScopes() {
    return includeScopes;
  }

  public ListProperty<String> getExcludeDependencies() {
    return excludeDependencies;
  }

  public ListProperty<String> getIgnoreSourcePackages() {
    return ignoreSourcePackages;
  }

  public ListProperty<String> getTargetSourcePackages() {
    return targetSourcePackages;
  }

  public ListProperty<String> getIgnoreDestinationPackages() {
    return ignoreDestinationPackages;
  }

  public ListProperty<String> getTargetDestinationPackages() {
    return targetDestinationPackages;
  }

  public void excludeDependency(String dependency) {
    this.excludeDependencies.add(dependency);
  }

  public void ignoreSourcePackage(String packageName) {
    this.ignoreSourcePackages.add(packageName);
  }

  public void targetSourcePackage(String packageName) {
    this.targetSourcePackages.add(packageName);
  }

  public void ignoreDestinationPackage(String packageName) {
    this.ignoreDestinationPackages.add(packageName);
  }

  public void targetDestinationPackage(String packageName) {
    this.targetDestinationPackages.add(packageName);
  }

  /**
   * Returns the reports configuration for the MissingLink plugin.
   *
   * @return The reports configuration.
   */
  public MissingLinkReports getReports() {
    return reports;
  }

  /**
   * Configures the reports using a DSL block.
   *
   * @param configureAction The action to configure the reports.
   */
  public void reports(Action<? super MissingLinkReports> configureAction) {
    configureAction.execute(reports);
  }
}
