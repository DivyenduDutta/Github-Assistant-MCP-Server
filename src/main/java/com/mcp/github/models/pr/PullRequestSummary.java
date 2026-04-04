package com.mcp.github.models.pr;

import java.util.List;

/**
 * A record representing a summary of a GitHub pull request, containing key information about the
 * pull request such as its number, title, state, author, branches involved, labels, comment count,
 * and various status flags. This record is designed to provide a concise overview of a pull
 * request.
 *
 * @param number The unique identifier of the pull request.
 * @param title The title of the pull request.
 * @param state The current state of the pull request (e.g., "open", "closed").
 * @param author The username of the author who created the pull request.
 * @param headBranch The name of the source branch for the pull request.
 * @param baseBranch The name of the target branch for the pull request.
 * @param labels A list of labels associated with the pull request.
 * @param commentCount The number of comments on the pull request.
 * @param daysOpen The number of days the pull request has been open.
 * @param daysSinceLastUpdate The number of days since the pull request was last updated.
 * @param isDraft A flag indicating whether the pull request is a draft.
 * @param isMerged A flag indicating whether the pull request has been merged.
 * @param isStale A flag indicating whether the pull request is considered stale based on its last
 *     update time.
 */
public record PullRequestSummary(
    int number,
    String title,
    String state,
    String author,
    String headBranch, // source branch
    String baseBranch, // target branch
    List<String> labels,
    int commentCount,
    int daysOpen,
    int daysSinceLastUpdate,
    boolean isDraft,
    boolean isMerged,
    boolean isStale) {

  public PullRequestSummary {
    labels = labels == null ? List.of() : List.copyOf(labels);
  }
}
