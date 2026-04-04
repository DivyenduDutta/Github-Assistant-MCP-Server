package com.mcp.github.models.pr;

import java.util.List;

public record PullRequestDetail(
    int number,
    String title,
    String body,
    String state,
    String author,
    String headBranch, // source branch
    String baseBranch, // target branch
    List<String> labels,
    boolean isDraft,
    boolean isMerged,
    boolean isStale,
    int daysOpen,
    int daysSinceLastUpdate,

    // Large PR, higher risk
    int additions,
    int deletions,
    int commits,
    int changedFiles,
    boolean isLarge,
    int pullRequestCommentCount,
    List<PullRequestComment> pullRequestComments) {

  public PullRequestDetail {
    labels = labels == null ? List.of() : List.copyOf(labels);
    pullRequestComments =
        pullRequestComments == null ? List.of() : List.copyOf(pullRequestComments);
  }
}
