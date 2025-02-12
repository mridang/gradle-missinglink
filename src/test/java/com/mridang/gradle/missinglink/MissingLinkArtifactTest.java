package com.mridang.gradle.missinglink;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import org.gradle.api.artifacts.ModuleIdentifier;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.artifacts.ResolvedModuleVersion;
import org.gradle.api.artifacts.component.ComponentArtifactIdentifier;
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
  private static class TestResolvedArtifact implements ResolvedArtifact {
    private final String group;
    private final String name;
    private final String version;
    private final File file;

    TestResolvedArtifact(String group, String name, String version, File file) {
      this.group = group;
      this.name = name;
      this.version = version;
      this.file = file;
    }

    @Override
    public File getFile() {
      return file;
    }

    @Override
    public ResolvedModuleVersion getModuleVersion() {
      return new TestResolvedModuleVersion(group, version);
    }

    @Override
    public String getName() {
      return name;
    }

    @Override
    public String getType() {
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
    public ComponentArtifactIdentifier getId() {
      throw new UnsupportedOperationException("Not implemented");
    }
  }

  /** Simple implementation of {@link ResolvedModuleVersion} for testing. */
  private static class TestResolvedModuleVersion implements ResolvedModuleVersion {
    private final String group;
    private final String version;

    TestResolvedModuleVersion(String group, String version) {
      this.group = group;
      this.version = version;
    }

    @Override
    public org.gradle.api.artifacts.ModuleVersionIdentifier getId() {
      return new org.gradle.api.artifacts.ModuleVersionIdentifier() {
        @Override
        public String getGroup() {
          return group;
        }

        @Override
        public String getName() {
          return "test-module";
        }

        @Override
        public String getVersion() {
          return version;
        }

        @Override
        public ModuleIdentifier getModule() {
          throw new UnsupportedOperationException("Not supported yet.");
        }
      };
    }
  }
}
