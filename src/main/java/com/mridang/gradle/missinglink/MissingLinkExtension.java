package com.mridang.gradle.missinglink;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Collections;
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
  private final ListProperty<String> excludeDependencies;
  private final MissingLinkReports reports;

  @SuppressWarnings("InjectOnConstructorOfAbstractClass")
  @SuppressFBWarnings("CT_CONSTRUCTOR_THROW")
  @Inject
  public MissingLinkExtension(Project project, ObjectFactory objectFactory) {
    this.skip = objectFactory.property(Boolean.class).convention(false);
    this.failOnConflicts = objectFactory.property(Boolean.class).convention(false);
    this.includeCategories =
        objectFactory.listProperty(String.class).convention(Collections.emptyList());
    this.excludeDependencies =
        objectFactory.listProperty(String.class).convention(Collections.emptyList());
    this.reports = objectFactory.newInstance(MissingLinkReportsImpl.class, project, objectFactory);
  }

  @SuppressFBWarnings("EI_EXPOSE_REP")
  public Property<Boolean> getSkip() {
    return skip;
  }

  @SuppressFBWarnings("EI_EXPOSE_REP")
  public Property<Boolean> getFailOnConflicts() {
    return failOnConflicts;
  }

  public ListProperty<String> getIncludeCategories() {
    return includeCategories;
  }

  public ListProperty<String> getExcludeDependencies() {
    return excludeDependencies;
  }

  public void excludeDependency(String dependency) {
    this.excludeDependencies.add(dependency);
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
