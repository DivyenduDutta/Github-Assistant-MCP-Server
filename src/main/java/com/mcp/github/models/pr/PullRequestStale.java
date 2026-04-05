package com.mcp.github.models.pr;

/**
 * A record representing the stale status of a GitHub pull request, containing information about
 * whether the pull request is a draft, whether it has been merged, whether it is considered stale,
 * the number of days it has been open, and the number of days since it was last updated. This
 * record is designed to provide insights into the activity and status of a pull request.
 *
 * @param isDraft A flag indicating whether the pull request is a draft.
 * @param isMerged A flag indicating whether the pull request has been merged.
 * @param isStale A flag indicating whether the pull request is considered stale based on its last
 *     update time.
 * @param daysOpen The number of days the pull request has been open.
 * @param daysSinceLastUpdate The number of days since the pull request was last updated.
 */
public record PullRequestStale(
    boolean isDraft, boolean isMerged, boolean isStale, int daysOpen, int daysSinceLastUpdate) {}
