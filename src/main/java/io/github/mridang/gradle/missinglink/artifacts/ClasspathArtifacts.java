package io.github.mridang.gradle.missinglink.artifacts;

import com.spotify.missinglink.ArtifactLoader;
import com.spotify.missinglink.datamodel.Artifact;
import io.github.mridang.gradle.missinglink.MissingLinkArtifact;
import io.github.mridang.gradle.missinglink.MissingLinkExclusions;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

/**
 * Represents the collection of resolved artifacts for analysis.
 *
 * @param artifacts The list of artifacts.
 */
public record ClasspathArtifacts(List<MissingLinkArtifact> artifacts) {

  private static final ArtifactLoader ARTIFACT_LOADER = new ArtifactLoader();
  private static final Logger LOGGER = Logging.getLogger(ClasspathArtifacts.class);

  /**
   * Constructs artifacts from the project's runtime classpath.
   *
   * @param project The Gradle project instance.
   */
  public ClasspathArtifacts(Project project) {
    this(
        project
            .getConfigurations()
            .getByName("runtimeClasspath")
            .getResolvedConfiguration()
            .getResolvedArtifacts()
            .stream()
            .map(MissingLinkArtifact::fromResolvedArtifact)
            .peek(artifact -> LOGGER.debug("Resolved artifact: {}", artifact))
            .toList());
  }

  public ClasspathArtifacts(List<MissingLinkArtifact> artifacts) {
    this.artifacts = List.copyOf(artifacts);
  }

  @SuppressWarnings("unused")
  @Override
  public List<MissingLinkArtifact> artifacts() {
    return Collections.unmodifiableList(artifacts);
  }

  /**
   * Filters the artifacts based on exclusions.
   *
   * @param exclusions The exclusion rules.
   * @return A new {@code MissingLinkArtifacts} instance with filtered artifacts.
   */
  public ClasspathArtifacts filter(MissingLinkExclusions exclusions) {
    return new ClasspathArtifacts(
        artifacts.stream().filter(artifact -> !exclusions.test(artifact)).toList());
  }

  /**
   * Converts the filtered artifacts into Spotify MissingLink Artifacts.
   *
   * @return A list of artifacts compatible with the MissingLink library.
   */
  public List<Artifact> toMissingLinkArtifacts() {
    return artifacts.stream()
        .map(
            artifact -> {
              try {
                LOGGER.trace("Loading artifact: {}", artifact.file());
                return ARTIFACT_LOADER.load(artifact.toArtifactName(), artifact.file());
              } catch (IOException e) {
                throw new RuntimeException("Failed to load artifact: " + artifact.file(), e);
              }
            })
        .toList();
  }
}
