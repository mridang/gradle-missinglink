package io.github.mridang.gradle.missinglink;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskProvider;

public class MissingLinkPlugin implements Plugin<Project> {

  public static final String EXTENSION_NAME = "missingLink";
  public static final String TASK_NAME = "missingLinkCheck";

  @Override
  public void apply(Project project) {
    project.getPluginManager().apply("java");
    MissingLinkExtension extension =
        project
            .getExtensions()
            .create(EXTENSION_NAME, MissingLinkExtension.class, project, project.getObjects());

    TaskProvider<MissingLinkTask> missingLinkTask =
        project
            .getTasks()
            .register(
                TASK_NAME,
                MissingLinkTask.class,
                task -> {
                  task.setGroup("verification");
                  task.setDescription("Checks for missing link conflicts in dependencies.");
                  task.getFailOnConflicts().set(extension.getFailOnConflicts());
                  task.getReports()
                      .getHtml()
                      .getRequired()
                      .set(extension.getReports().getHtml().getRequired());
                  task.getReports()
                      .getXml()
                      .getRequired()
                      .set(extension.getReports().getXml().getRequired());
                  task.getReports()
                      .getSarif()
                      .getRequired()
                      .set(extension.getReports().getSarif().getRequired());
                  task.getReports()
                      .getHtml()
                      .getOutputLocation()
                      .set(extension.getReports().getHtml().getOutputLocation());
                  task.getReports()
                      .getXml()
                      .getOutputLocation()
                      .set(extension.getReports().getXml().getOutputLocation());
                  task.getReports()
                      .getSarif()
                      .getOutputLocation()
                      .set(extension.getReports().getSarif().getOutputLocation());
                  task.getClasspath()
                      .from(project.getConfigurations().getByName("runtimeClasspath"));
                });

    project.afterEvaluate(
        p ->
            project
                .getTasks()
                .named("check")
                .configure(checkTask -> checkTask.dependsOn(missingLinkTask)));

    project.getTasks().named("check").configure(checkTask -> checkTask.dependsOn(missingLinkTask));
  }
}
