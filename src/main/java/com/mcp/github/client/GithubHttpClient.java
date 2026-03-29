package com.mcp.github.client;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * A simple HTTP client for interacting with the GitHub API. It handles authentication using a
 * personal access token and provides methods to fetch data from GitHub. The client uses Java's
 * built-in HttpClient for making HTTP requests and Jackson's ObjectMapper for parsing JSON
 * responses.
 */
@Component
public class GithubHttpClient {
  private final HttpClient httpClient;

  private static final String GITHUB_API_BASE_URL = "https://api.github.com";

  @SuppressFBWarnings(
      value = "EI_EXPOSE_REP2",
      justification =
          "The objectMapper will not be modified outside of this class since its private.")
  private final ObjectMapper objectMapper;

  @Value("${github.token}")
  private String token;

  public GithubHttpClient(HttpClient httpClient, ObjectMapper objectMapper) {
    this.httpClient = httpClient;
    this.objectMapper = objectMapper;
  }

  /**
   * Helper method to perform an authenticated GET request to the GitHub API. It constructs the
   * request with the necessary headers, sends it, and parses the response into a JsonNode.
   *
   * @param url The URL to send the GET request to.
   * @return A JsonNode representing the parsed JSON response from the GitHub API.
   * @throws RuntimeException if there is an error during the HTTP request or JSON parsing.
   */
  private JsonNode get(String url) {
    try {
      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(URI.create(url))
              .header("Authorization", "Bearer " + token)
              .header("Accept", "application/vnd.github+json")
              .GET()
              .build();

      HttpResponse<String> response =
          httpClient.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() != 200) {
        throw new RuntimeException(
            "GitHub API error: " + response.statusCode() + " - " + response.body());
      }

      return objectMapper.readTree(response.body());

    } catch (Exception e) {
      throw new RuntimeException("GitHub API call failed" + e.getMessage());
    }
  }

  /**
   * Fetches a list of issues from a specified GitHub repository. It constructs the appropriate API
   * URL, sends an authenticated GET request, and parses the JSON response into a JsonNode for
   * further processing. Retrieves only open issues.
   *
   * @param owner The owner of the repository (e.g., "octocat").
   * @param repo The name of the repository (e.g., "Hello-World").
   * @return A JsonNode representing the list of issues returned by the GitHub API.
   */
  public JsonNode getIssues(String owner, String repo) {
    String url = String.format(GITHUB_API_BASE_URL + "/repos/%s/%s/issues", owner, repo);
    return get(url);
  }

  /**
   * Fetches the details of a specific issue from a GitHub repository. It constructs the API URL
   * using the repository owner, name, and issue number, sends an authenticated GET request, and
   * parses the JSON response into a JsonNode for further processing. Can retrieve details of both
   * open and closed issues.
   *
   * @param owner The owner of the repository (e.g., "octocat").
   * @param repo The name of the repository (e.g., "Hello-World").
   * @param issueNumber The number of the issue to fetch details for.
   * @return A JsonNode representing the details of the specified issue returned by the GitHub API.
   * @throws IllegalArgumentException if issueNumber is less than 1.
   */
  public JsonNode getIssue(String owner, String repo, int issueNumber) {
    if (issueNumber < 1) {
      throw new IllegalArgumentException("Issue number must be positive");
    }

    String url =
        String.format(GITHUB_API_BASE_URL + "/repos/%s/%s/issues/%d", owner, repo, issueNumber);
    return get(url);
  }

  /**
   * Fetches the comments of a specific issue from a GitHub repository. It constructs the API URL
   * using the repository owner, name, and issue number, sends an authenticated GET request, and
   * parses the JSON response into a JsonNode for further processing.
   *
   * @param owner The owner of the repository (e.g., "octocat").
   * @param repo The name of the repository (e.g., "Hello-World").
   * @param issueNumber The number of the issue to fetch comments for.
   * @return A JsonNode representing the list of comments for the specified issue returned by the
   *     GitHub API.
   * @throws IllegalArgumentException if issueNumber is less than 1.
   */
  public JsonNode getIssueComments(String owner, String repo, int issueNumber) {
    if (issueNumber < 1) {
      throw new IllegalArgumentException("Issue number must be positive");
    }

    String url =
        String.format(
            GITHUB_API_BASE_URL + "/repos/%s/%s/issues/%d/comments", owner, repo, issueNumber);
    return get(url);
  }

  /**
   * Fetches a list of pull requests from a specified GitHub repository. It constructs the API URL
   * using the repository owner and name, sends an authenticated GET request, and parses the JSON
   * response into a JsonNode for further processing. Retrieves all pull requests (open and closed)
   * sorted by last updated time in descending order.
   *
   * @param owner The owner of the repository (e.g., "octocat").
   * @param repo The name of the repository (e.g., "Hello-World").
   * @param page The page number to fetch (for pagination).
   * @param perPage The number of pull requests to return per page.
   * @return A JsonNode representing the list of pull requests returned by the GitHub API.
   * @throws IllegalArgumentException if page or perPage is less than 1.
   */
  public JsonNode getPullRequests(String owner, String repo, int page, int perPage) {
    if (page < 1 || perPage < 1) {
      throw new IllegalArgumentException("page and perPage must be positive");
    }

    String url =
        String.format(
            GITHUB_API_BASE_URL
                + "/repos/%s/%s/pulls?state=all&sort=updated&direction=desc&page=%d&per_page=%d",
            owner,
            repo,
            page,
            perPage);
    return get(url);
  }
}
