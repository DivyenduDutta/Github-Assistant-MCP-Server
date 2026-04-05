package com.mcp.github.models.issue;

/**
 * Represents the stale status of a GitHub issue, indicating how many days the issue has been open
 * and whether it is considered stale.
 *
 * @param daysOpen The number of days the issue has been open.
 * @param isStale A boolean indicating whether the issue is considered stale or not.
 */
public record IssueStale(int daysOpen, boolean isStale) {}
