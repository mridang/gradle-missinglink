package io.github.mridang.gradle.missinglink.reports;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.spotify.missinglink.Conflict;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/** Generates an HTML report for MissingLink dependency conflicts. */
public class HTMLReport {

  private static final ObjectWriter XML_MAPPER =
      new XmlMapper().writerWithDefaultPrettyPrinter().withRootName("tbody");
  private final TableBody tableBody;

  /**
   * Constructs an HTML report from a list of conflicts.
   *
   * @param conflicts The list of detected conflicts.
   */
  public HTMLReport(List<Conflict> conflicts) {
    this.tableBody =
        new TableBody(
            conflicts.stream()
                .map(
                    conflict ->
                        new TableRow(
                            conflict.category().name(),
                            conflict.reason(),
                            conflict.dependency().describe(),
                            conflict.existsIn().name(),
                            conflict.usedBy().name()))
                .toList());
  }

  /**
   * Writes the report to an HTML file.
   *
   * @param file The output file.
   */
  public void writeToFile(File file) {
    try {
      Files.writeString(
          file.toPath(),
          """
      <html>
      <head>
          <title>MissingLink Report</title>
          <style>
              table { border-collapse: collapse; width: 100%%; }
              th, td { border: 1px solid black; padding: 8px; text-align: left; }
              th { background-color: #f2f2f2; }
          </style>
      </head>
      <body>
          <h1>MissingLink Dependency Conflict Report</h1>
          <table>
              <thead>
                  <tr>
                      <th>Category</th>
                      <th>Reason</th>
                      <th>Dependency</th>
                      <th>Exists In</th>
                      <th>Used By</th>
                  </tr>
              </thead>"""
              + XML_MAPPER.writeValueAsString(tableBody).replaceAll("td.*?td", "td")
              + """
          </table>
      </body>
      </html>""");
    } catch (IOException e) {
      throw new RuntimeException("Failed to write HTML report", e);
    }
  }

  /** Represents the body of the HTML table (generated dynamically). */
  @JacksonXmlRootElement(localName = "tbody")
  protected record TableBody(
      @JacksonXmlElementWrapper(useWrapping = false) @JsonProperty("tr") List<TableRow> rows) {

    public TableBody(List<TableRow> rows) {
      this.rows = List.copyOf(rows);
    }
  }

  /** Represents a row in the HTML table. */
  protected record TableRow(
      @JsonProperty("tdcategorytd") String category,
      @JsonProperty("tdreasontd") String reason,
      @JsonProperty("tddependencytd") String dependency,
      @JsonProperty("tdexistsIntd") String existsIn,
      @JsonProperty("tdusedBytd") String usedBy) {
    //
  }
}
