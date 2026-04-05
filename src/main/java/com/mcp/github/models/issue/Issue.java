package com.mcp.github.models.issue;

/**
 * Represents a GitHub issue with its basic details.
 *
 * @param issueBasicDetails The basic details of the issue, such as title, body, labels, etc.
 */
public record Issue(IssueBasic issueBasicDetails) {}
