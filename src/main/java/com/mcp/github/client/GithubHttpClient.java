package com.mcp.github.client;

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
 * personal access token and provides methods to fetch data from GitHub, such as issues from a
 * repository. The client uses Java's built-in HttpClient for making HTTP requests and Jackson's
 * ObjectMapper for parsing JSON responses.
 */
@Component
public class GithubHttpClient {
  private final HttpClient httpClient;
  private final ObjectMapper objectMapper;

  @Value("${github.token}")
  private String token;

  public GithubHttpClient(HttpClient httpClient, ObjectMapper objectMapper) {
    this.httpClient = httpClient;
    this.objectMapper = objectMapper;
  }

  /**
   * Fetches a list of issues from a specified GitHub repository. It constructs the appropriate API
   * URL, sends an authenticated GET request, and parses the JSON response into a JsonNode for
   * further processing.
   *
   * @param owner The owner of the repository (e.g., "octocat").
   * @param repo The name of the repository (e.g., "Hello-World").
   * @return A JsonNode representing the list of issues returned by the GitHub API.
   * @throws RuntimeException if there is an error during the HTTP request or JSON parsing.
   */
  public JsonNode getIssues(String owner, String repo) {
    try {
      String url = String.format("https://api.github.com/repos/%s/%s/issues", owner, repo);

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
      throw new RuntimeException("Failed to fetch issues from GitHub " + e.getMessage());
    }
  }
}
