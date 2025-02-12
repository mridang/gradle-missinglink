package com.mridang.gradle.missinglink.reports;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.spotify.missinglink.Conflict;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.gradle.internal.impldep.com.fasterxml.jackson.annotation.JsonRootName;

/** Generates an XML report for MissingLink dependency conflicts. */
public class XMLReport {

  private static final ObjectWriter XML_MAPPER =
      new XmlMapper().writerWithDefaultPrettyPrinter().withRootName("report");
  private final List<ConflictRecord> conflicts;

  /**
   * Constructs an XML report from a list of conflicts.
   *
   * @param conflicts The list of detected conflicts.
   */
  public XMLReport(List<Conflict> conflicts) {
    this.conflicts =
        conflicts.stream()
            .map(
                conflict ->
                    new ConflictRecord(
                        conflict.reason(),
                        conflict.category(),
                        new DependencyRecord(
                            conflict.dependency().fromClass().getClassName(),
                            conflict.dependency().fromMethod().pretty(),
                            conflict.dependency().fromLineNumber(),
                            conflict.dependency().targetClass().getClassName(),
                            conflict.dependency().describe()),
                        conflict.existsIn().name(),
                        conflict.usedBy().name()))
            .toList();
  }

  /**
   * Writes the XML report to a file.
   *
   * @param file The file to which the report should be written.
   */
  public void writeToFile(File file) {
    try {
      XML_MAPPER.writeValue(file, new ReportWrapper(this.conflicts));
    } catch (IOException e) {
      throw new RuntimeException("Failed to write XML report", e);
    }
  }

  @JsonRootName("report")
  public record ReportWrapper(@JsonProperty("conflicts") List<ConflictRecord> conflicts) {

    public ReportWrapper(
        @SuppressWarnings("ClassEscapesDefinedScope") List<ConflictRecord> conflicts) {
      this.conflicts = List.copyOf(conflicts);
    }
  }

  /**
   * Represents a conflict record in the XML report.
   *
   * @param reason The reason for the conflict.
   * @param category The conflict category.
   * @param dependency The dependency causing the conflict.
   * @param existsIn The artifact where the conflict exists.
   * @param usedBy The artifact using the conflicting dependency.
   */
  protected record ConflictRecord(
      @JsonProperty("reason") String reason,
      @JsonProperty("category") Conflict.ConflictCategory category,
      @JsonProperty("dependency") DependencyRecord dependency,
      @JsonProperty("existsIn") String existsIn,
      @JsonProperty("usedBy") String usedBy) {
    //
  }

  /**
   * Represents a dependency record in the XML report.
   *
   * @param fromClass The class where the dependency originates.
   * @param fromMethod The method where the dependency is used.
   * @param fromLine The line number in the source code where it appears.
   * @param targetClass The class that is being depended on.
   * @param description A human-readable description of the dependency.
   */
  private record DependencyRecord(
      @JsonProperty("fromClass") String fromClass,
      @JsonProperty("fromMethod") String fromMethod,
      @JsonProperty("fromLine") int fromLine,
      @JsonProperty("targetClass") String targetClass,
      @JsonProperty("description") String description) {
    //
  }

  /**
   * Returns the list of conflicts for serialization.
   *
   * @return The list of conflicts.
   */
  @JsonProperty("missinglink-report")
  List<ConflictRecord> getConflicts() {
    return conflicts;
  }
}
