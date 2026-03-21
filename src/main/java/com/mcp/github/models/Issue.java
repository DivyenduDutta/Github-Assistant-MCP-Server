package com.mcp.github.models;

/**
 * Represents a GitHub issue with relevant details.
 *
 * @param number     The issue number.
 * @param title      The title of the issue.
 * @param state      The current state of the issue (e.g., open, closed).
 * @param author     The username of the issue's author.
 * @param createdAt  The timestamp when the issue was created.
 * @param daysOpen   The number of days the issue has been open.
 */
public record Issue(
        int number,
        String title,
        String state,
        String author,
        String createdAt,
        int daysOpen
) {}
