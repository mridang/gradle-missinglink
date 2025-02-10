package com.mridang.gradle.missinglink.reports;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.spotify.missinglink.Conflict;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class SARIFReport {

  private static final ObjectWriter OBJECT_MAPPER =
      new ObjectMapper()
          .setSerializationInclusion(JsonInclude.Include.NON_NULL)
          .writerWithDefaultPrettyPrinter();
  private static final String SARIF_VERSION = "2.1.0";
  private final SarifRoot sarifRoot;

  public SARIFReport(List<Conflict> conflicts) {
    List<SarifResult> results =
        conflicts.stream().map(conflict -> new SarifResult(conflict.reason())).toList();
    this.sarifRoot = new SarifRoot(SARIF_VERSION, List.of(new SarifRun(results)));
  }

  /** Serializes the conflicts to a SARIF JSON file. */
  public void writeToFile(File file) {
    try {
      OBJECT_MAPPER.writeValue(file, sarifRoot);
    } catch (IOException e) {
      throw new RuntimeException("Failed to write SARIF report", e);
    }
  }

  public SarifRoot getSarifRoot() {
    return sarifRoot;
  }

  /** SARIF Root structure. */
  protected static record SarifRoot(
      @JsonProperty("sarif") String version, @JsonProperty("runs") List<SarifRun> runs) {
    //
  }

  /** SARIF Run structure. */
  protected static record SarifRun(@JsonProperty("results") List<SarifResult> results) {
    //
  }

  /** SARIF Result structure. */
  protected static record SarifResult(@JsonProperty("message") String message) {
    //
  }
}
