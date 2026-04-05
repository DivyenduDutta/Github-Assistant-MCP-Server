package com.mcp.github.models.pr;

/**
 * A record representing a comment on a GitHub pull request, containing information about the author
 * of the comment, the content of the comment, and the timestamp when the comment was created. This
 * record is designed to encapsulate the details of a pull request comment for use in various
 * contexts, such as displaying comments or analyzing comment activity.
 *
 * @param author The username of the author who made the comment.
 * @param body The content of the comment.
 * @param createdAt The timestamp when the comment was created, typically in ISO 8601 format.
 */
public record PullRequestComment(String author, String body, String createdAt) {}
