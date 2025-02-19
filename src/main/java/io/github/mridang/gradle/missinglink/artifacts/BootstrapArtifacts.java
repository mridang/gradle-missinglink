package io.github.mridang.gradle.missinglink.artifacts;

import com.spotify.missinglink.Java9ModuleLoader;
import com.spotify.missinglink.datamodel.Artifact;
import java.util.Collections;
import java.util.List;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

/**
 * Represents Java 9 module artifacts for analysis.
 *
 * @param artifacts The list of resolved Java 9 module artifacts.
 */
public record BootstrapArtifacts(List<Artifact> artifacts) {

  private static final Logger LOGGER = Logging.getLogger(BootstrapArtifacts.class);

  /** Constructs artifacts from Java 9 module paths. */
  public BootstrapArtifacts() {
    this(
        Java9ModuleLoader.getJava9ModuleArtifacts(
                (msg, ex) -> {
                  if (ex != null) {
                    LOGGER.warn("Error loading Java 9 module artifact: {}", msg, ex);
                  } else {
                    LOGGER.info("Java 9 module artifact loaded: {}", msg);
                  }
                })
            .stream()
            .peek(artifact -> LOGGER.debug("Resolved bootstrap artifact: {}", artifact))
            .toList());
  }

  public BootstrapArtifacts(List<Artifact> artifacts) {
    this.artifacts = List.copyOf(artifacts);
  }

  @Override
  public List<Artifact> artifacts() {
    return Collections.unmodifiableList(artifacts);
  }

  /**
   * Converts Java 9 module artifacts into Spotify MissingLink Artifacts.
   *
   * @return A list of artifacts compatible with the MissingLink library.
   */
  public List<Artifact> toMissingLinkArtifacts() {
    return artifacts();
  }
}
