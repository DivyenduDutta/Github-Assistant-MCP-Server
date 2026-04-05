package com.mcp.github.models.pr;

import java.util.List;

/**
 * A record representing detailed information about a GitHub pull request, including basic details,
 * branch information, staleness status, size metrics, the body of the pull request, comment count,
 * and a list of comments. This record is designed to provide a comprehensive view of a pull request
 * for analysis.
 *
 * @param pullRequestBasicDetails Basic information about the pull request, such as its number,
 *     title, state, author, and labels.
 * @param pullRequestBranchDetails Information about the source and target branches involved in the
 *     pull request.
 * @param pullRequestStaleDetails Information about the staleness status of the pull request.
 * @param pullRequestSizeDetails Metrics related to the size of the pull request, such as lines
 *     added and removed.
 * @param body The body text of the pull request, which may contain a description or additional
 *     information provided by the author.
 * @param pullRequestCommentCount The total number of comments on the pull request.
 * @param pullRequestComments A list of comments associated with the pull request, providing
 *     detailed feedback or discussion points from reviewers or other contributors.
 */
public record PullRequestDetail(
    PullRequestBasic pullRequestBasicDetails,
    PullRequestBranch pullRequestBranchDetails,
    PullRequestStale pullRequestStaleDetails,
    PullRequestSize pullRequestSizeDetails,
    String body,
    int pullRequestCommentCount,
    List<PullRequestComment> pullRequestComments) {

  // Required to ensure this record is an immutable contract
  public PullRequestDetail {
    pullRequestComments =
        pullRequestComments == null ? List.of() : List.copyOf(pullRequestComments);
  }
}
