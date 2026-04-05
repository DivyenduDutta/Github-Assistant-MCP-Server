package com.mcp.github.models.pr;

/**
 * A record representing the size of a GitHub pull request, including the number of additions,
 * deletions, commits, changed files, and a flag indicating whether the pull request is considered
 * large. This record is designed to provide insights into the scope and potential risk of a pull
 * request based on its size.
 *
 * @param additions The number of lines of code added in the pull request.
 * @param deletions The number of lines of code deleted in the pull request.
 * @param commits The number of commits included in the pull request.
 * @param changedFiles The number of files changed in the pull request.
 * @param isLarge A flag indicating whether the pull request is considered large based on its size
 *     metrics.
 */
public record PullRequestSize(
    // Large PR, higher risk
    int additions, int deletions, int commits, int changedFiles, boolean isLarge) {}
