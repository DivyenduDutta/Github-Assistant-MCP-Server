package com.mcp.github.models.issue;

import java.util.List;

/**
 * Represents the detailed information of a GitHub issue, including its basic details, stale status,
 * body content, comment count, and comments.
 *
 * @param issueBasicDetails The basic details of the issue, such as title, body, labels, etc.
 * @param issueStaleDetails The stale status of the issue, indicating if it is considered stale or
 *     not.
 * @param body The body content of the issue.
 * @param commentCount The number of comments on the issue.
 * @param comments The list of comments on the issue.
 */
public record IssueDetail(
    IssueBasic issueBasicDetails,
    IssueStale issueStaleDetails,
    String body,
    int commentCount,
    List<IssueComment> comments) {

  // Required to ensure this record is an immutable contract
  public IssueDetail {
    comments = comments == null ? List.of() : List.copyOf(comments);
  }
}
