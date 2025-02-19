package io.github.mridang.gradle.missinglink;

import com.spotify.missinglink.datamodel.ArtifactName;
import java.io.File;
import org.gradle.api.artifacts.ResolvedArtifact;

/**
 * Represents an individual artifact with metadata.
 *
 * @param group The artifact's group.
 * @param name The artifact's name.
 * @param version The artifact's version.
 * @param file The resolved file.
 */
public record MissingLinkArtifact(String group, String name, String version, File file) {

  /**
   * Creates a {@code MissingLinkArtifact} from a resolved artifact.
   *
   * @param artifact The Gradle resolved artifact.
   * @return The {@code MissingLinkArtifact} representation.
   */
  public static MissingLinkArtifact fromResolvedArtifact(ResolvedArtifact artifact) {
    return new MissingLinkArtifact(
        artifact.getModuleVersion().getId().getGroup(),
        artifact.getName(),
        artifact.getModuleVersion().getId().getVersion(),
        artifact.getFile());
  }

  /**
   * Converts to a Spotify MissingLink {@code ArtifactName}.
   *
   * @return The artifact name in the required format.
   */
  public ArtifactName toArtifactName() {
    return new ArtifactName("%s:%s:%s".formatted(group, name, version));
  }
}
