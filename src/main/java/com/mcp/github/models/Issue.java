package com.mcp.github.models;

public record Issue(
        int number,
        String title,
        String state,
        String author,
        String createdAt,
        int daysOpen
) {}
