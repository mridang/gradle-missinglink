package io.github.mridang.gradle.missinglink;

import java.util.List;
import java.util.function.Predicate;
import org.gradle.api.Project;

/**
 * Represents a set of excluded artifacts, allowing users to exclude dependencies by full name.
 *
 * @param excludePatterns A list of exclusion patterns from the Gradle extension.
 */
public record MissingLinkExclusions(List<String> excludePatterns)
    implements Predicate<MissingLinkArtifact> {

  /**
   * Constructs exclusions from the project configuration.
   *
   * @param project The Gradle project instance.
   */
  public MissingLinkExclusions(Project project) {
    this(
        project
            .getExtensions()
            .getByType(MissingLinkExtension.class)
            .getExcludeDependencies()
            .get());
  }

  public MissingLinkExclusions(List<String> excludePatterns) {
    this.excludePatterns = List.copyOf(excludePatterns);
  }

  /**
   * Checks if a given artifact is excluded based on its group, name, or version.
   *
   * @param artifact The artifact metadata.
   * @return {@code true} if the artifact matches an exclusion pattern, otherwise {@code false}.
   */
  @Override
  public boolean test(MissingLinkArtifact artifact) {
    return excludePatterns.stream()
        .anyMatch(
            exclusion -> exclusion.equals("%s:%s".formatted(artifact.group(), artifact.name())));
  }
}
