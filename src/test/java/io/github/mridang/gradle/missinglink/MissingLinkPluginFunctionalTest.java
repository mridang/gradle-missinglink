package io.github.mridang.gradle.missinglink;

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.UnexpectedBuildFailure;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.io.TempDir;

@SuppressWarnings("checkstyle:LineLength")
class MissingLinkPluginFunctionalTest {

  /**
   * Runs the Gradle build and checks if the `missingLinkCheck` task succeeds.
   *
   * @param testProjectDir The test's temporary directory.
   * @param testInfo The JUnit test information.
   * @param buildScript The contents of the build.gradle file.
   * @param javaSource The Java source file content.
   * @return {@code true} if the `missingLinkCheck` task succeeds, otherwise {@code false}.
   * @throws IOException If writing to files fails.
   */
  private boolean runGradleAndCheckTaskSuccess(
      Path testProjectDir, TestInfo testInfo, String buildScript, String javaSource)
      throws IOException {

    String projectName = testInfo.getDisplayName().replace("()", "").replace(" ", "-");

    Files.writeString(
        testProjectDir.resolve("settings.gradle"), "rootProject.name = '" + projectName + "'");

    Files.writeString(testProjectDir.resolve("build.gradle"), buildScript);

    Path srcDir = Files.createDirectories(testProjectDir.resolve("src/main/java/com/example"));
    System.out.println(srcDir);
    Files.writeString(srcDir.resolve("TestClass.java"), javaSource);

    System.out.println("Project is at " + testProjectDir.toFile());
    try {
      BuildResult result =
          GradleRunner.create()
              .withProjectDir(testProjectDir.toFile())
              .withArguments("classes", "missingLinkCheck")
              .withPluginClasspath()
              .forwardOutput()
              .build(); // This will throw an exception if the build fails

      return Optional.ofNullable(result.task(":missingLinkCheck"))
          .map(task -> task.getOutcome() == SUCCESS)
          .orElse(false);

    } catch (UnexpectedBuildFailure e) {
      System.err.println("Gradle build failed: " + e.getMessage());
      return false;
    }
  }

  @Test
  void testMissingLinkTaskRunsWhenThereAreNoErrors(@TempDir Path testProjectDir, TestInfo testInfo)
      throws IOException {
    boolean result =
        runGradleAndCheckTaskSuccess(
            testProjectDir,
            testInfo,
            """
                plugins {
                    id 'java'
                    id 'io.github.mridang.gradle.missinglink'
                }

                repositories {
                    mavenCentral()
                }

                dependencies {
                    implementation(group: 'com.fasterxml.jackson.core', name: 'jackson-databind', version: '2.18.2') {
                        transitive = false
                    }
                }

                missingLink {
                    failOnConflicts = true
                }
            """,
            """
            package com.example;

            public class TestClass {

                public void hello() {
                    System.out.println("Hello, world!");
                }
            }
            """);

    assertTrue(result);
    assertFalse(Files.exists(testProjectDir.resolve("build/reports/missinglink/missinglink.html")));
  }

  @Test
  void testMissingLinkTaskRunsSuccessfully(@TempDir Path testProjectDir, TestInfo testInfo)
      throws IOException {
    boolean result =
        runGradleAndCheckTaskSuccess(
            testProjectDir,
            testInfo,
            """
                plugins {
                    id 'java'
                    id 'io.github.mridang.gradle.missinglink'
                }

                repositories {
                    mavenCentral()
                }

                dependencies {
                    implementation group: 'com.fasterxml.jackson.core', name: 'jackson-databind', version: '2.18.2'
                }

                missingLink {
                    failOnConflicts = false
                }
            """,
            """
            package com.example;

            import com.fasterxml.jackson.databind.ObjectMapper;

            public class TestClass {
                private final ObjectMapper objectMapper = new ObjectMapper();

                public void hello() {
                    System.out.println("Hello, world!");
                }
            }
            """);

    assertTrue(result);
    assertFalse(Files.exists(testProjectDir.resolve("build/reports/missinglink/missinglink.html")));
  }

  @Test
  void testMissingLinkTaskWithExcludedDependencies(@TempDir Path testProjectDir, TestInfo testInfo)
      throws IOException {
    boolean result =
        runGradleAndCheckTaskSuccess(
            testProjectDir,
            testInfo,
            """
                plugins {
                    id 'java'
                    id 'io.github.mridang.gradle.missinglink'
                }

                repositories {
                    mavenCentral()
                }

                dependencies {
                    implementation(group: 'com.fasterxml.jackson.core', name: 'jackson-databind', version: '2.18.2') {
                        transitive = false
                    }
                }

                missingLink {
                    failOnConflicts = true
                    excludeDependencies = ['com.fasterxml.*']
                    reports {
                        html {
                            required = true
                        }
                        xml {
                            required = true
                        }
                        sarif {
                            required = true
                        }
                    }
                }
            """,
            """
            package com.example;

            import com.fasterxml.jackson.databind.ObjectMapper;

            public class TestClass {
                private final ObjectMapper objectMapper = new ObjectMapper();

                public void hello() {
                    System.out.println("Hello, world!");
                }
            }
            """);

    assertFalse(result);
    assertTrue(Files.exists(testProjectDir.resolve("build/reports/missinglink/missinglink.sarif")));
    assertTrue(Files.exists(testProjectDir.resolve("build/reports/missinglink/missinglink.xml")));
    assertTrue(Files.exists(testProjectDir.resolve("build/reports/missinglink/missinglink.html")));
  }

  @Test
  void testThatReportLocationCanBeChanged(@TempDir Path testProjectDir, TestInfo testInfo)
      throws IOException {
    boolean result =
        runGradleAndCheckTaskSuccess(
            testProjectDir,
            testInfo,
            """
                plugins {
                    id 'java'
                    id 'io.github.mridang.gradle.missinglink'
                }

                repositories {
                    mavenCentral()
                }

                dependencies {
                    implementation(group: 'com.fasterxml.jackson.core', name: 'jackson-databind', version: '2.18.2') {
                        transitive = false
                    }
                }

                missingLink {
                    failOnConflicts = true
                    reports {
                        html {
                            required = true
                            outputLocation = file("$buildDir/reports/html/missinglink.html")
                        }
                        xml {
                            required = true
                            outputLocation = file("$buildDir/reports/xml/missinglink.xml")
                        }
                        sarif {
                            required = true
                            outputLocation = file("$buildDir/reports/sarif/missinglink.sarif")
                        }
                    }
                }
            """,
            """
            package com.example;

            import com.fasterxml.jackson.databind.ObjectMapper;

            public class TestClass {
                private final ObjectMapper objectMapper = new ObjectMapper();

                public void hello() {
                    System.out.println("Hello, world!");
                }
            }
            """);

    assertFalse(result);
    assertTrue(Files.exists(testProjectDir.resolve("build/reports/sarif/missinglink.sarif")));
    assertTrue(Files.exists(testProjectDir.resolve("build/reports/xml/missinglink.xml")));
    assertTrue(Files.exists(testProjectDir.resolve("build/reports/html/missinglink.html")));
  }

  @Test
  void testMissingLinkTaskWithExcludedDependencieReportsOfds(
      @TempDir Path testProjectDir, TestInfo testInfo) throws IOException {
    boolean result =
        runGradleAndCheckTaskSuccess(
            testProjectDir,
            testInfo,
            """
                plugins {
                    id 'java'
                    id 'io.github.mridang.gradle.missinglink'
                }

                repositories {
                    mavenCentral()
                }

                dependencies {
                    implementation(group: 'com.fasterxml.jackson.core', name: 'jackson-databind', version: '2.18.2') {
                        transitive = false
                    }
                }

                missingLink {
                    failOnConflicts = true
                    excludeDependencies = ['com.fasterxml.jackson.core:jackson-databind']
                    reports {
                        html {
                            required = false
                        }
                        xml {
                            required = false
                        }
                        sarif {
                            required = false
                        }
                    }
                }
            """,
            """
            package com.example;

            import com.fasterxml.jackson.databind.ObjectMapper;

            public class TestClass {
                private final ObjectMapper objectMapper = new ObjectMapper();

                public void hello() {
                    System.out.println("Hello, world!");
                }
            }
            """);

    assertTrue(result);
    assertFalse(
        Files.exists(testProjectDir.resolve("build/reports/missinglink/missinglink.sarif")));
    assertFalse(Files.exists(testProjectDir.resolve("build/reports/missinglink/missinglink.xml")));
    assertFalse(Files.exists(testProjectDir.resolve("build/reports/missinglink/missinglink.html")));
  }
}
