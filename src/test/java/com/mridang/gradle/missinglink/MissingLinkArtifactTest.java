package com.mridang.gradle.missinglink;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import org.gradle.api.artifacts.ModuleIdentifier;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.artifacts.ResolvedModuleVersion;
import org.gradle.api.artifacts.component.ComponentArtifactIdentifier;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

/** Unit tests for the {@link MissingLinkArtifact} record. */
class MissingLinkArtifactTest {

  /**
   * Tests that a {@link MissingLinkArtifact} is correctly created from a {@link ResolvedArtifact}.
   */
  @Test
  void testMissingLinkArtifactFromResolvedArtifact() {
    File artifactFile = new File("dependency.jar");
    ResolvedArtifact resolvedArtifact =
        new TestResolvedArtifact("org.example", "example-lib", "2.3.4", artifactFile);

    MissingLinkArtifact artifact = MissingLinkArtifact.fromResolvedArtifact(resolvedArtifact);

    assertEquals("org.example", artifact.group());
    assertEquals("example-lib", artifact.name());
    assertEquals("2.3.4", artifact.version());
    assertEquals(artifactFile, artifact.file());
    assertEquals("org.example:example-lib:2.3.4", artifact.toArtifactName().name());
  }

  /** Simple implementation of {@link ResolvedArtifact} for testing purposes. */
  @SuppressWarnings("SameParameterValue")
  private record TestResolvedArtifact(String group, String name, String version, File file)
      implements ResolvedArtifact {

    @Override
    public @NotNull File getFile() {
      return file;
    }

    @Override
    public @NotNull ResolvedModuleVersion getModuleVersion() {
      return new TestResolvedModuleVersion(group, version);
    }

    @Override
    public @NotNull String getName() {
      return name;
    }

    @Override
    public @NotNull String getType() {
      return "jar";
    }

    @Override
    public String getExtension() {
      return "jar";
    }

    @SuppressWarnings("NullAway")
    @Override
    public String getClassifier() {
      return null;
    }

    @Override
    public @NotNull ComponentArtifactIdentifier getId() {
      throw new UnsupportedOperationException("Not implemented");
    }
  }

  /** Simple implementation of {@link ResolvedModuleVersion} for testing. */
  private record TestResolvedModuleVersion(String group, String version)
      implements ResolvedModuleVersion {

    @Override
    public org.gradle.api.artifacts.@NotNull ModuleVersionIdentifier getId() {
      return new org.gradle.api.artifacts.ModuleVersionIdentifier() {
        @Override
        public @NotNull String getGroup() {
          return group;
        }

        @Override
        public @NotNull String getName() {
          return "test-module";
        }

        @Override
        public @NotNull String getVersion() {
          return version;
        }

        @Override
        public @NotNull ModuleIdentifier getModule() {
          throw new UnsupportedOperationException("Not supported yet.");
        }
      };
    }
  }
}
