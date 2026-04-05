package com.mcp.github.models.issue;

import java.util.List;

/**
 * Represents the basic details of a GitHub issue, including its number, title, state, author, and
 * labels.
 *
 * @param number The unique identifier of the issue.
 * @param title The title of the issue.
 * @param state The current state of the issue (e.g., open, closed).
 * @param author The username of the issue's author.
 * @param labels A list of labels associated with the issue.
 */
public record IssueBasic(
    int number, String title, String state, String author, List<String> labels) {

  // Required to ensure this record is an immutable contract
  public IssueBasic {
    labels = labels == null ? List.of() : List.copyOf(labels);
  }
}
