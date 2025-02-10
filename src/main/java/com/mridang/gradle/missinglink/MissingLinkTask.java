package com.mridang.gradle.missinglink;

import com.mridang.gradle.missinglink.artifacts.BootstrapArtifacts;
import com.mridang.gradle.missinglink.artifacts.ClasspathArtifacts;
import com.mridang.gradle.missinglink.reports.HTMLReport;
import com.mridang.gradle.missinglink.reports.SARIFReport;
import com.mridang.gradle.missinglink.reports.XMLReport;
import com.spotify.missinglink.ArtifactLoader;
import com.spotify.missinglink.Conflict;
import com.spotify.missinglink.ConflictChecker;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import groovy.lang.Closure;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Inject;
import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.reporting.Reporting;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Classpath;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.TaskAction;
import org.gradle.util.ConfigureUtil;

@CacheableTask
public abstract class MissingLinkTask extends DefaultTask implements Reporting<MissingLinkReports> {

  @Input
  public abstract Property<Boolean> getFailOnConflicts();

  @Input
  public abstract ListProperty<String> getIgnoreSourcePackages();

  @Input
  public abstract ListProperty<String> getIgnoreDestinationPackages();

  @Classpath
  public abstract ConfigurableFileCollection getClasspath();

  private final MissingLinkReports reports;

  @SuppressWarnings("InjectOnConstructorOfAbstractClass")
  @SuppressFBWarnings("CT_CONSTRUCTOR_THROW")
  @Inject
  public MissingLinkTask(Project project, ObjectFactory objectFactory) {
    reports = objectFactory.newInstance(MissingLinkReportsImpl.class, project, objectFactory);
    dependsOn("classes");
  }

  @Override
  @Nested
  public MissingLinkReports getReports() {
    return reports;
  }

  @Override
  public MissingLinkReports reports(Action<? super MissingLinkReports> configureAction) {
    configureAction.execute(reports);
    return reports;
  }

  @Override
  public MissingLinkReports reports(Closure configureClosure) {
    Action<? super MissingLinkReports> action = ConfigureUtil.configureUsing(configureClosure);
    action.execute(reports);
    return reports;
  }

  @TaskAction
  public void runMissingLinkCheck() throws IOException {
    getLogger().lifecycle("Running MissingLink dependency conflict check...");

    var exclusions = new MissingLinkExclusions(getProject());
    var artifacts = new ClasspathArtifacts(getProject()).filter(exclusions);
    var bootstraps = new BootstrapArtifacts();

    ConflictChecker checker = new ConflictChecker();

    var conflicts =
        checker.check(
            new ArtifactLoader()
                .load(
                    getProject()
                        .getLayout()
                        .getBuildDirectory()
                        .dir("classes/java/main")
                        .get()
                        .getAsFile()),
            artifacts.toMissingLinkArtifacts(),
            Stream.concat(
                    artifacts.toMissingLinkArtifacts().stream(),
                    bootstraps.toMissingLinkArtifacts().stream())
                .toList());

    conflicts = filterConflicts(conflicts);

    if (!conflicts.isEmpty()) {
      getLogger().error(conflicts.size() + " conflicts found!");

      if (reports.getHtml().getRequired().get()) {
        System.out.println("Html enabled");
        new HTMLReport(conflicts)
            .writeToFile(reports.getHtml().getOutputLocation().get().getAsFile());
      }
      if (reports.getXml().getRequired().get()) {
        System.out.println("xml enabled");
        new XMLReport(conflicts)
            .writeToFile(reports.getXml().getOutputLocation().get().getAsFile());
      }
      if (reports.getSarif().getRequired().get()) {
        System.out.println("sarif enabled");
        new SARIFReport(conflicts)
            .writeToFile(reports.getSarif().getOutputLocation().get().getAsFile());
      }

      if (getFailOnConflicts().get()) {
        throw new GradleException("MissingLink found conflicts!");
      }
    } else {
      getLogger().lifecycle("No conflicts found.");
    }
  }

  private List<Conflict> filterConflicts(List<Conflict> conflicts) {
    List<String> ignoreSource = getIgnoreSourcePackages().get();
    List<String> ignoreDestination = getIgnoreDestinationPackages().get();

    return conflicts.stream()
        .filter(
            conflict ->
                !ignoreSource.contains(
                    extractPackageName(conflict.dependency().fromClass().getClassName())))
        .filter(
            conflict ->
                !ignoreDestination.contains(
                    extractPackageName(conflict.dependency().targetClass().getClassName())))
        .collect(Collectors.toList());
  }

  /**
   * Extracts the package name from a fully qualified class name.
   *
   * @param className The fully qualified class name (e.g., "com.example.MyClass").
   * @return The package name (e.g., "com.example"), or an empty string if there is no package.
   */
  private String extractPackageName(String className) {
    int lastDot = className.lastIndexOf('.');
    return (lastDot == -1) ? "" : className.substring(0, lastDot);
  }
}
