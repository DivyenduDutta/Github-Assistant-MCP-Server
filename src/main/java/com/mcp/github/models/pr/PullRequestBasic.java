package com.mcp.github.models.pr;

import java.util.List;

/**
 * A record representing the basic information of a GitHub pull request, including its number,
 * title, state, author, and associated labels. This record serves as a foundational data structure
 * for representing pull request details in a concise manner.
 *
 * @param number The unique identifier of the pull request.
 * @param title The title of the pull request.
 * @param state The current state of the pull request (e.g., "open", "closed").
 * @param author The username of the author who created the pull request.
 * @param labels A list of labels associated with the pull request.
 */
public record PullRequestBasic(
    int number, String title, String state, String author, List<String> labels) {

  // Required to ensure this record is an immutable contract
  public PullRequestBasic {
    labels = labels == null ? List.of() : List.copyOf(labels);
  }
}
