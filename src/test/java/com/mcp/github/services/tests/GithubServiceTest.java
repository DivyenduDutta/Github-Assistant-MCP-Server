package com.mcp.github.services.tests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import com.mcp.github.client.GithubHttpClient;
import com.mcp.github.models.Issue;
import com.mcp.github.models.IssueDetail;
import com.mcp.github.services.GithubService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

public class GithubServiceTest {

  private GithubHttpClient githubHttpClient;
  private ObjectMapper objectMapper;
  private GithubService githubService;

  @BeforeEach
  void setUp() throws Exception {
    githubHttpClient = Mockito.mock(GithubHttpClient.class);
    objectMapper = new ObjectMapper();
    githubService = new GithubService(githubHttpClient);
  }

  @Test
  public void testListIssues() {
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
    JsonNode res = objectMapper.readTree(mockResponse);

    Mockito.doReturn(res).when(githubHttpClient).getIssues(any(String.class), any(String.class));

    List<Issue> issues = githubService.listIssues("owner", "repo");
    assertEquals(1, issues.size());
    Issue issue = issues.get(0);
    assertEquals("Test Issue", issue.title());

    Mockito.verify(githubHttpClient).getIssues(any(String.class), any(String.class));
  }

  @Test
  public void testListIssuesLimited() {
    String mockResponse =
        """
                    [
                      {
                        "number": 1,
                        "title": "Test Issue",
                        "state": "open",
                        "user": {"login": "testuser"},
                        "created_at": "2024-01-01T00:00:00Z"
                      },
                      {
                        "number": 2,
                        "title": "Test Issue 2",
                        "state": "open",
                        "user": {"login": "testuser2"},
                        "created_at": "2024-02-01T00:00:00Z"
                      },
                      {
                        "number": 3,
                        "title": "Test Issue 3",
                        "state": "open",
                        "user": {"login": "testuser3"},
                        "created_at": "2024-03-01T00:00:00Z"
                      },
                      {
                        "number": 4,
                        "title": "Test Issue 4",
                        "state": "open",
                        "user": {"login": "testuser4"},
                        "created_at": "2024-04-01T00:00:00Z"
                      },
                      {
                        "number": 5,
                        "title": "Test Issue 5",
                        "state": "open",
                        "user": {"login": "testuser5"},
                        "created_at": "2024-05-01T00:00:00Z"
                      },
                      {
                        "number": 6,
                        "title": "Test Issue 6",
                        "state": "open",
                        "user": {"login": "testuser6"},
                        "created_at": "2024-06-01T00:00:00Z"
                      }
                    ]
                """;
    JsonNode res = objectMapper.readTree(mockResponse);

    Mockito.doReturn(res).when(githubHttpClient).getIssues(any(String.class), any(String.class));

    List<Issue> issues = githubService.listIssues("owner", "repo", 3);
    assertEquals(3, issues.size());
    Issue issue = issues.get(0);
    assertEquals("Test Issue", issue.title());

    Mockito.verify(githubHttpClient).getIssues(any(String.class), any(String.class));
  }

  @Test
  public void testGetIssue() {
    String mockIssueResponse =
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

    String mockIssueCommentsResponse =
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
    JsonNode resIssue = objectMapper.readTree(mockIssueResponse);
    JsonNode resIssueComments = objectMapper.readTree(mockIssueCommentsResponse);

    Mockito.doReturn(resIssue)
        .when(githubHttpClient)
        .getIssue(any(String.class), any(String.class), any(int.class));
    Mockito.doReturn(resIssueComments)
        .when(githubHttpClient)
        .getIssueComments(any(String.class), any(String.class), any(int.class));

    IssueDetail issue = githubService.getIssue("owner", "repo", 10);
    assertEquals("Test Issue", issue.title());
    assertEquals("testuser", issue.comments().get(0).author());

    Mockito.verify(githubHttpClient).getIssue(any(String.class), any(String.class), any(int.class));
    Mockito.verify(githubHttpClient)
        .getIssueComments(any(String.class), any(String.class), any(int.class));
  }
}
