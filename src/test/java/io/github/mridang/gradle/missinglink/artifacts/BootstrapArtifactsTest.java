package io.github.mridang.gradle.missinglink.artifacts;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

class BootstrapArtifactsTest {

  @Test
  void shouldLoadJava9ModuleArtifacts() {
    BootstrapArtifacts bootstrapArtifacts = new BootstrapArtifacts();
    assertFalse(bootstrapArtifacts.toMissingLinkArtifacts().isEmpty());
  }
}
