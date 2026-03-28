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
   * further processing.
   *
   * @param owner The owner of the repository (e.g., "octocat").
   * @param repo The name of the repository (e.g., "Hello-World").
   * @return A JsonNode representing the list of issues returned by the GitHub API.
   */
  public JsonNode getIssues(String owner, String repo) {
    String url = String.format("https://api.github.com/repos/%s/%s/issues", owner, repo);
    return get(url);
  }

  /**
   * Fetches the details of a specific issue from a GitHub repository. It constructs the API URL
   * using the repository owner, name, and issue number, sends an authenticated GET request, and
   * parses the JSON response into a JsonNode for further processing.
   *
   * @param owner The owner of the repository (e.g., "octocat").
   * @param repo The name of the repository (e.g., "Hello-World").
   * @param issueNumber The number of the issue to fetch details for.
   * @return A JsonNode representing the details of the specified issue returned by the GitHub API.
   */
  public JsonNode getIssue(String owner, String repo, int issueNumber) {
    String url =
        String.format("https://api.github.com/repos/%s/%s/issues/%d", owner, repo, issueNumber);
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
   */
  public JsonNode getIssueComments(String owner, String repo, int issueNumber) {
    String url =
        String.format(
            "https://api.github.com/repos/%s/%s/issues/%d/comments", owner, repo, issueNumber);
    return get(url);
  }
}
