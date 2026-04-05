package com.mcp.github.constants;

/**
 * A utility class that defines constants used throughout the GitHub Assistant application. This
 * class centralizes important values, making it easier to maintain and update these values in one
 * place.
 */
public final class GithubConstants {
  private GithubConstants() {
    // Prevent instantiation
  }

  public static final int DEFAULT_PER_PAGE = 30; // GitHub's default items per page
  public static final int DEFAULT_NUMBER_OF_PAGES_TO_FETCH =
      1; // Default Number of pages to fetch for pagination
}
