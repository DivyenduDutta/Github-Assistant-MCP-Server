package com.mcp.github.models;

/**
 * A simple data class representing a comment on a GitHub issue. This class is used to encapsulate
 * the information about comments fetched from the GitHub API, making it easier to work with
 * comments in the application.
 *
 * @param author The username of the comment's author.
 * @param body The content of the comment.
 */
public record IssueComment(String author, String body) {}
