package com.mcp.github.models.pr;

/**
 * A record representing the branch information of a GitHub pull request, including the source
 * branch (head) and the target branch (base). This record is used to encapsulate the details of the
 * branches involved in a pull request.
 *
 * @param headBranch The name of the source branch for the pull request ie, merged from this branch.
 * @param baseBranch The name of the target branch for the pull request ie, merged into this branch.
 */
public record PullRequestBranch(
    String headBranch, // source branch
    String baseBranch // target branch
    ) {}
