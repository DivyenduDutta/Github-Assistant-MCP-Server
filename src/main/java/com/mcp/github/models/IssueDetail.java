package com.mcp.github.models;

import java.util.List;

/**
 * A record representing detailed information about a GitHub issue. This record is used to provide a
 * comprehensive view of an issue when requested by the assistant.
 *
 * @param number The issue number.
 * @param title The title of the issue.
 * @param body The body or description of the issue.
 * @param state The current state of the issue (e.g., "open", "closed").
 * @param author The username of the issue's author.
 * @param labels A list of labels associated with the issue.
 * @param commentCount The number of comments on the issue.
 * @param comments A list of recent comments on the issue.
 * @param daysOpen The number of days the issue has been open.
 * @param isStale A boolean indicating whether the issue is considered stale (open for more than a
 *     certain threshold of days).
 */
public record IssueDetail(
    int number,
    String title,
    String body,
    String state,
    String author,
    List<String> labels,
    int commentCount,
    List<IssueComment> comments,
    int daysOpen,
    boolean isStale) {

  // Required to ensure this record is an immutable contract
  public IssueDetail {
    labels = labels == null ? List.of() : List.copyOf(labels);
    comments = comments == null ? List.of() : List.copyOf(comments);
  }
}
