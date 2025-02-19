package io.github.mridang.gradle.missinglink.reports;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
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

class XMLReportTest {

  private static final ObjectReader XML_READER =
      new XmlMapper().readerFor(XMLReport.ReportWrapper.class);

  private static Conflict createConflict(ConflictCategory category, String reason, int lineNumber) {
    ClassTypeDescriptor inClass = TypeDescriptors.fromClassName("com/example/ClassC");

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
                ConflictCategory.METHOD_SIGNATURE_NOT_FOUND, "Dependency X is missing", 50),
            createConflict(ConflictCategory.CLASS_NOT_FOUND, "Version mismatch in Y", 60));
    XMLReport report = new XMLReport(conflicts);

    File outputFile = new File(tempDir, "report.xml");
    report.writeToFile(outputFile);

    XMLReport.ReportWrapper actualConflicts = XML_READER.readValue(outputFile);

    assertEquals(report.getConflicts(), actualConflicts.conflicts());
  }
}
