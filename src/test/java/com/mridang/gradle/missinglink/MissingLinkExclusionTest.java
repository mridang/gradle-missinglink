package com.mridang.gradle.missinglink;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Unit tests for the {@link MissingLinkExclusions} predicate. */
class MissingLinkExclusionTest {

  /** Tests that an excluded artifact is correctly identified. */
  @Test
  void testExcludedArtifact() {
    MissingLinkExclusions exclusions =
        new MissingLinkExclusions(List.of("org.example:excluded-lib"));
    MissingLinkArtifact artifact =
        new MissingLinkArtifact("org.example", "excluded-lib", "1.0.0", new File("lib.jar"));

    assertTrue(exclusions.test(artifact), "Artifact should be excluded");
  }

  /** Tests that a non-excluded artifact is not mistakenly excluded. */
  @Test
  void testNonExcludedArtifact() {
    MissingLinkExclusions exclusions =
        new MissingLinkExclusions(List.of("org.example:excluded-lib"));
    MissingLinkArtifact artifact =
        new MissingLinkArtifact("com.example", "allowed-lib", "1.0.0", new File("lib.jar"));

    assertFalse(exclusions.test(artifact), "Artifact should NOT be excluded");
  }
}
