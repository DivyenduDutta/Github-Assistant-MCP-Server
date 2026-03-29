package com.mcp.github.client.tests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import com.mcp.github.client.GithubHttpClient;
import java.lang.reflect.Field;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

public class GithubHttpClientTest {
  private HttpClient httpClient;
  private ObjectMapper objectMapper;
  private GithubHttpClient githubHttpClient;

  @BeforeEach
  void setUp() throws Exception {
    httpClient = Mockito.mock(HttpClient.class);
    objectMapper = new ObjectMapper();

    githubHttpClient = new GithubHttpClient(httpClient, objectMapper);

    // Inject token manually (since @Value won’t run in unit test)
    Field tokenField = GithubHttpClient.class.getDeclaredField("token");
    tokenField.setAccessible(true);
    tokenField.set(githubHttpClient, "test-token");
  }

  @Test
  void testGetIssuesPositive() throws Exception {
    String mockResponse =
        """
            [
              {
                "number": 1,
                "title": "Test Issue",
                "state": "open",
                "user": {"login": "testuser"},
                "created_at": "2024-01-01T00:00:00Z"
              }
            ]
        """;

    HttpResponse<String> response = Mockito.mock(HttpResponse.class);

    Mockito.when(response.statusCode()).thenReturn(200);
    Mockito.when(response.body()).thenReturn(mockResponse);

    Mockito.doReturn(response)
        .when(httpClient)
        .send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));

    JsonNode result = githubHttpClient.getIssues("owner", "repo");

    assertNotNull(result);
    assertTrue(result.isArray());
    assertEquals(1, result.size());
    assertEquals("Test Issue", result.get(0).get("title").asString());
  }

  @Test
  void testGetIssuesNegative() throws Exception {
    HttpResponse<String> response = Mockito.mock(HttpResponse.class);

    Mockito.when(response.statusCode()).thenReturn(401);
    Mockito.when(response.body()).thenReturn("Unauthorized");

    Mockito.doReturn(response)
        .when(httpClient)
        .send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));

    RuntimeException ex =
        assertThrows(RuntimeException.class, () -> githubHttpClient.getIssues("owner", "repo"));

    assertTrue(ex.getMessage().contains("GitHub API error"));
  }

  @Test
  void testGetIssuePositive() throws Exception {
    String mockResponse =
        """
                {
                    "number": 1,
                    "title": "Test Issue",
                    "body" : "This is a test issue.",
                    "state": "open",
                    "user": {"login": "testuser"},
                    "created_at": "2024-01-01T00:00:00Z",
                    "labels": [{"name": "bug"}, {"name": "feature"}]
                }
                """;

    HttpResponse<String> response = Mockito.mock(HttpResponse.class);

    Mockito.when(response.statusCode()).thenReturn(200);
    Mockito.when(response.body()).thenReturn(mockResponse);

    Mockito.doReturn(response)
        .when(httpClient)
        .send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));

    JsonNode result = githubHttpClient.getIssue("owner", "repo", 10);

    assertNotNull(result);
    assertFalse(result.isArray());
    assertEquals("bug", result.get("labels").asArray().get(0).get("name").asString());
  }

  @Test
  void testGetIssueNegative() throws Exception {
    HttpResponse<String> response = Mockito.mock(HttpResponse.class);

    Mockito.when(response.statusCode()).thenReturn(401);
    Mockito.when(response.body()).thenReturn("Unauthorized");

    Mockito.doReturn(response)
        .when(httpClient)
        .send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));

    RuntimeException ex =
        assertThrows(RuntimeException.class, () -> githubHttpClient.getIssue("owner", "repo", 10));

    assertTrue(ex.getMessage().contains("GitHub API error"));
  }

  @Test
  void testGetIssueCommentsPositive() throws Exception {
    String mockResponse =
        """
                [
                    {
                        "user": {"login": "testuser"},
                        "body": "This is test issue comment."
                    },
                    {
                        "user": {"login": "testuser1"},
                        "body": "This is another test issue comment."
                    }
                ]
                """;

    HttpResponse<String> response = Mockito.mock(HttpResponse.class);

    Mockito.when(response.statusCode()).thenReturn(200);
    Mockito.when(response.body()).thenReturn(mockResponse);

    Mockito.doReturn(response)
        .when(httpClient)
        .send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));

    JsonNode result = githubHttpClient.getIssue("owner", "repo", 10);

    assertNotNull(result);
    assertTrue(result.isArray());
    assertEquals(2, result.size());
    assertEquals("testuser1", result.get(1).get("user").get("login").asString());
  }

  @Test
  void testGetIssueCommentsNegative() throws Exception {
    HttpResponse<String> response = Mockito.mock(HttpResponse.class);

    Mockito.when(response.statusCode()).thenReturn(401);
    Mockito.when(response.body()).thenReturn("Unauthorized");

    Mockito.doReturn(response)
        .when(httpClient)
        .send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));

    RuntimeException ex =
        assertThrows(RuntimeException.class, () -> githubHttpClient.getIssue("owner", "repo", 10));

    assertTrue(ex.getMessage().contains("GitHub API error"));
  }

  @Test
  void testGetPullRequestsPositive() throws Exception {
    String mockResponse =
        """
                        [
                            {
                                "number": 1,
                                "title": "Test PR 1",
                                "state": "open",
                                "user": {"login": "testuser"},
                                "head": {
                                    "ref": "feature-branch",
                                    "sha": "abc123"
                                },
                                 "base": {
                                    "ref": "main",
                                    "sha": "def456"
                                },
                                 "labels": [{"name": "bug"}, {"name": "feature"}],
                                 "comments": 5,
                                 "created_at": "2024-01-01T00:00:00Z",
                                "updated_at": "2024-01-02T00:00:00Z",
                                "draft": false,
                                "merged_at": "2024-01-03T00:00:00Z"
                            },
                            {
                                "number": 2,
                                "title": "Test PR 2",
                                "state": "closed",
                                "user": {"login": "testuser2"},
                                "head": {
                                    "ref": "bugfix-branch",
                                    "sha": "abc123"
                                },
                                 "base": {
                                    "ref": "main",
                                    "sha": "def456"
                                },
                                 "labels": [{"name": "bug"}, {"name": "feature"}],
                                "created_at": "2024-01-01T00:00:00Z",
                                "updated_at": "2024-01-02T00:00:00Z",
                                "draft": true,
                                "merged_at": null
                            }
                        ]
                        """;
    HttpResponse<String> response = Mockito.mock(HttpResponse.class);

    Mockito.when(response.statusCode()).thenReturn(200);
    Mockito.when(response.body()).thenReturn(mockResponse);

    Mockito.doReturn(response)
        .when(httpClient)
        .send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));

    JsonNode result = githubHttpClient.getPullRequests("owner", "repo", 1, 10);

    assertNotNull(result);
    assertTrue(result.isArray());
    assertEquals(2, result.size());
    assertEquals("bug", result.get(0).get("labels").asArray().get(0).get("name").asString());
  }

  @Test
  void testGetPullRequestsNegative() throws Exception {
    HttpResponse<String> response = Mockito.mock(HttpResponse.class);

    Mockito.when(response.statusCode()).thenReturn(401);
    Mockito.when(response.body()).thenReturn("Unauthorized");

    Mockito.doReturn(response)
        .when(httpClient)
        .send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));

    IllegalArgumentException ex =
        assertThrows(
            IllegalArgumentException.class,
            () -> githubHttpClient.getPullRequests("owner", "repo", 1, -1));

    assertTrue(ex.getMessage().contains("page and perPage must be positive"));
  }
}
