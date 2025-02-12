package com.mridang.gradle.missinglink.reports;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.mridang.gradle.missinglink.reports.SARIFReport.SarifRoot;
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
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SARIFReportTest {

  private static final ObjectReader JSON_READER = new ObjectMapper().readerFor(SarifRoot.class);

  private static Conflict createConflict(
      Conflict.ConflictCategory category, String reason, int lineNumber) {
    ClassTypeDescriptor inClass = TypeDescriptors.fromClassName("com/example/ClassB");
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
  void testWriteToFile(@TempDir File tempDir) throws IOException {
    List<Conflict> conflicts =
        List.of(
            createConflict(
                ConflictCategory.METHOD_SIGNATURE_NOT_FOUND, "Missing method in ClassA", 30),
            createConflict(ConflictCategory.CLASS_NOT_FOUND, "Conflicting library version", 40));
    SARIFReport report = new SARIFReport(conflicts);

    File outputFile = new File(tempDir, "report.sarif");
    report.writeToFile(outputFile);

    SarifRoot sarifRoot = JSON_READER.readValue(outputFile);
    assertEquals(sarifRoot, report.sarifRoot);
  }
}
