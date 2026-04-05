package com.mcp.github.models.pr;

/**
 * A record representing a summary of a GitHub pull request, including basic details, branch
 * information, staleness status, and the total comment count. This record is designed to provide a
 * concise overview of a pull request for analysis.
 *
 * @param pullRequestBasicDetails Basic information about the pull request, such as its number,
 *     title, state, author, and labels.
 * @param pullRequestBranchDetails Information about the source and target branches involved in the
 *     pull request.
 * @param pullRequestStaleDetails Information about the staleness status of the pull request.
 */
public record PullRequestSummary(
    PullRequestBasic pullRequestBasicDetails,
    PullRequestBranch pullRequestBranchDetails,
    PullRequestStale pullRequestStaleDetails) {}
