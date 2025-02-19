package io.github.mridang.gradle.missinglink.reports;

import com.spotify.missinglink.Conflict;
import com.spotify.missinglink.Conflict.ConflictCategory;
import com.spotify.missinglink.ConflictBuilder;
import com.spotify.missinglink.ConflictChecker;
import com.spotify.missinglink.datamodel.ArtifactName;
import com.spotify.missinglink.datamodel.ClassTypeDescriptor;
import com.spotify.missinglink.datamodel.MethodDependencyBuilder;
import com.spotify.missinglink.datamodel.MethodDescriptorBuilder;
import com.spotify.missinglink.datamodel.TypeDescriptors;
import java.io.File;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class HTMLReportTest {

  private static Conflict createConflict(
      Conflict.ConflictCategory category, String reason, int lineNumber) {
    ClassTypeDescriptor inClass = TypeDescriptors.fromClassName("com/example/ClassA");
    return new ConflictBuilder()
        .category(category)
        .dependency(
            new MethodDependencyBuilder()
                .fromClass(inClass)
                .fromMethod(
                    new MethodDescriptorBuilder()
                        .name("callerMethod")
                        .returnType(TypeDescriptors.fromRaw("V"))
                        .parameterTypes(List.of())
                        .build())
                .fromLineNumber(lineNumber)
                .targetClass(inClass)
                .targetMethod(
                    new MethodDescriptorBuilder()
                        .name("calleeMethod")
                        .returnType(TypeDescriptors.fromRaw("V"))
                        .parameterTypes(List.of())
                        .build())
                .build())
        .reason(reason)
        .usedBy(new ArtifactName("source"))
        .existsIn(ConflictChecker.UNKNOWN_ARTIFACT_NAME)
        .build();
  }

  @Test
  void testWriteToFile(@TempDir File tempDir) {
    List<Conflict> conflicts =
        List.of(
            createConflict(
                ConflictCategory.METHOD_SIGNATURE_NOT_FOUND, "Missing method in ClassA", 30),
            createConflict(ConflictCategory.CLASS_NOT_FOUND, "Conflicting library version", 40));
    HTMLReport report = new HTMLReport(conflicts);

    File outputFile = new File(tempDir, "report.html");
    report.writeToFile(outputFile);
  }
}
