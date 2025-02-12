package com.mridang.gradle.missinglink;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/** Unit tests for the MissingLink Gradle plugin. */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MissingLinkPluginTest {

  /** Provides a preconfigured Project instance for tests. */
  private static Stream<Project> projectProvider() {
    Project project = ProjectBuilder.builder().build();
    project.getPlugins().apply(JavaPlugin.class);
    project.getPlugins().apply(MissingLinkPlugin.class);
    return Stream.of(project);
  }

  /** Tests that the MissingLink plugin registers the expected extension. */
  @ParameterizedTest
  @MethodSource("projectProvider")
  void testPluginRegistersExtension(Project project) {
    assertNotNull(project.getExtensions().findByName(MissingLinkPlugin.EXTENSION_NAME));
  }

  /** Tests that the MissingLink task is registered. */
  @ParameterizedTest
  @MethodSource("projectProvider")
  void testPluginRegistersTask(Project project) {
    Task missingLinkTask = project.getTasks().findByName(MissingLinkPlugin.TASK_NAME);
    assertNotNull(missingLinkTask);
    assertEquals("verification", missingLinkTask.getGroup());
    assertEquals(
        "Checks for missing link conflicts in dependencies.", missingLinkTask.getDescription());
  }

  /** Tests that the MissingLink task is registered as a dependency of the 'check' task. */
  @ParameterizedTest
  @MethodSource("projectProvider")
  void testCheckTaskDependsOnMissingLinkTask(Project project) {
    Task checkTask = project.getTasks().getByName("check");
    assertNotNull(checkTask);

    Task missingLinkTask = project.getTasks().getByName(MissingLinkPlugin.TASK_NAME);
    assertNotNull(missingLinkTask);

    Set<String> resolvedDependencies =
        checkTask.getTaskDependencies().getDependencies(checkTask).stream()
            .map(Task::getPath)
            .collect(Collectors.toSet());

    assertTrue(resolvedDependencies.contains(missingLinkTask.getPath()));
  }

  /** Tests the default value of failOnConflicts. */
  @ParameterizedTest
  @MethodSource("projectProvider")
  void testDefaultFailOnConflicts(Project project) {
    MissingLinkExtension extension =
        (MissingLinkExtension) project.getExtensions().findByName(MissingLinkPlugin.EXTENSION_NAME);

    assertNotNull(extension);
    assertTrue(extension.getFailOnConflicts().get(), "failOnConflicts should default to true");
  }

  /** Tests the default configuration for the HTML report. */
  @ParameterizedTest
  @MethodSource("projectProvider")
  void testDefaultHtmlReportConfiguration(Project project) {
    MissingLinkExtension extension =
        (MissingLinkExtension) project.getExtensions().findByName(MissingLinkPlugin.EXTENSION_NAME);

    assertNotNull(extension);
    assertTrue(
        extension.getReports().getHtml().getRequired().get(),
        "HTML report should be enabled by default");
  }

  /** Tests the default configuration for the XML report. */
  @ParameterizedTest
  @MethodSource("projectProvider")
  void testDefaultXmlReportConfiguration(Project project) {
    MissingLinkExtension extension =
        (MissingLinkExtension) project.getExtensions().findByName(MissingLinkPlugin.EXTENSION_NAME);

    assertNotNull(extension);
    assertTrue(
        extension.getReports().getXml().getRequired().get(),
        "XML report should be disabled by default");
  }

  /** Tests the default configuration for the SARIF report. */
  @ParameterizedTest
  @MethodSource("projectProvider")
  void testDefaultSarifReportConfiguration(Project project) {
    MissingLinkExtension extension =
        (MissingLinkExtension) project.getExtensions().findByName(MissingLinkPlugin.EXTENSION_NAME);

    assertNotNull(extension);
    assertTrue(
        extension.getReports().getSarif().getRequired().get(),
        "SARIF report should be disabled by default");
  }
}
