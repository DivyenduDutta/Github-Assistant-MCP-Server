package com.mcp.github.tools.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.mcp.github.models.Issue;
import com.mcp.github.models.IssueComment;
import com.mcp.github.models.IssueDetail;
import com.mcp.github.models.PullRequestSummary;
import com.mcp.github.services.GithubService;
import com.mcp.github.tools.GithubAssistantTools;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class GithubAssistantToolsTest {

  private GithubService githubService;
  private GithubAssistantTools tools;

  @BeforeEach
  void setUp() {
    githubService = Mockito.mock(GithubService.class);
    tools = new GithubAssistantTools(githubService);
  }

  @Test
  void testListIssues() {
    List<Issue> mockIssues =
        List.of(new Issue(1, "Test Issue", "open", "testuser", "2024-01-01T00:00:00Z", 10));

    Mockito.when(githubService.listIssues("owner", "repo")).thenReturn(mockIssues);

    List<Issue> result = tools.listIssues("owner", "repo");

    assertEquals(1, result.size());
    assertEquals("Test Issue", result.get(0).title());

    Mockito.verify(githubService).listIssues("owner", "repo");
  }

  @Test
  void testListIssuesLimited() {
    List<Issue> mockIssues =
        List.of(
            new Issue(1, "Test Issue", "open", "testuser", "2024-01-01T00:00:00Z", 10),
            new Issue(2, "Test Issue 2", "open", "testuser2", "2024-02-01T00:00:00Z", 20));

    Mockito.when(githubService.listIssues("owner", "repo", 2)).thenReturn(mockIssues);

    List<Issue> result = tools.listIssues("owner", "repo", 2);

    assertEquals(2, result.size());
    assertEquals("Test Issue 2", result.get(1).title());

    Mockito.verify(githubService).listIssues("owner", "repo", 2);
  }

  @Test
  void testGetIssue() {
    IssueDetail mockIssue =
        new IssueDetail(
            1,
            "Test Issue",
            "open",
            "open",
            "testuser",
            List.of("Feature", "Bug"),
            1,
            List.of(new IssueComment("testuser", "This is a test issue comment")),
            10,
            false);

    Mockito.when(githubService.getIssue("owner", "repo", 10)).thenReturn(mockIssue);

    IssueDetail issue = tools.getIssue("owner", "repo", 10);

    assertEquals("Feature", issue.labels().get(0));

    Mockito.verify(githubService).getIssue("owner", "repo", 10);
  }

  @Test
  void testListPullRequests() {
    List<PullRequestSummary> mockPullRequests =
        List.of(
            new PullRequestSummary(
                1,
                "Test PR",
                "open",
                "testuser",
                "main",
                "feature-branch",
                List.of("Enhancement"),
                5,
                10,
                2,
                false,
                false,
                false));

    Mockito.when(githubService.listPullRequests("owner", "repo", 1, 30))
        .thenReturn(mockPullRequests);

    List<PullRequestSummary> result = tools.listPullRequests("owner", "repo", null, null);

    assertEquals(1, result.size());
    assertEquals("Test PR", result.get(0).title());

    Mockito.verify(githubService).listPullRequests("owner", "repo", 1, 30);
  }
}
