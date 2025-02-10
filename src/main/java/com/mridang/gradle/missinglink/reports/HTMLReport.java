package com.mridang.gradle.missinglink.reports;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.spotify.missinglink.Conflict;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class HTMLReport {
  private final HtmlRoot htmlRoot;

  public HTMLReport(List<Conflict> conflicts) {
    this.htmlRoot =
        new HtmlRoot(conflicts.stream().map(conflict -> new HtmlRow(conflict.reason())).toList());
  }

  public void writeToFile(File file) {
    ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    try {
      String json = objectMapper.writeValueAsString(htmlRoot);

      // Convert JSON into an HTML table format
      String htmlContent =
          """
                <html>
                <head><title>MissingLink Report</title></head>
                <body>
                <h1>MissingLink Dependency Conflict Report</h1>
                <table border=\"1\">
                <tr><th>Conflict</th></tr>
                """
              + json.replace("{\"rows\":[", "")
                  .replace("]}", "")
                  .replace("{\"message\":", "<tr><td>")
                  .replace("},", "</td></tr>")
                  .replace("}", "</td></tr>")
              + """
                </table>
                </body>
                </html>
                """;

      objectMapper.writeValue(file, htmlContent);
    } catch (IOException e) {
      throw new RuntimeException("Failed to write HTML report", e);
    }
  }

  public record HtmlRoot(@JsonProperty("rows") List<HtmlRow> rows) {}

  public record HtmlRow(
      @JsonProperty("message") @JsonSerialize(using = ToStringSerializer.class) String message) {}
}
