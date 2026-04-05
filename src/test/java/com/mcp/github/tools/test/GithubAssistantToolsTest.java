package com.mcp.github.tools.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mcp.github.models.issue.*;
import com.mcp.github.models.pr.*;
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
        List.of(
            new Issue(
                new IssueBasic(1, "Test Issue", "open", "testuser", List.of("feature", "bug"))));

    Mockito.when(githubService.listIssues("owner", "repo")).thenReturn(mockIssues);

    List<Issue> result = tools.listIssues("owner", "repo");

    assertEquals(1, result.size());
    assertEquals("Test Issue", result.get(0).issueBasicDetails().title());

    Mockito.verify(githubService).listIssues("owner", "repo");
  }

  @Test
  void testListIssuesLimited() {
    List<Issue> mockIssues =
        List.of(
            new Issue(
                new IssueBasic(1, "Test Issue", "open", "testuser", List.of("feature", "bug"))),
            new Issue(new IssueBasic(2, "Test Issue 2", "open", "testuser2", List.of("feature"))));

    Mockito.when(githubService.listIssues("owner", "repo", 2)).thenReturn(mockIssues);

    List<Issue> result = tools.listIssues("owner", "repo", 2);

    assertEquals(2, result.size());
    assertEquals("Test Issue 2", result.get(1).issueBasicDetails().title());

    Mockito.verify(githubService).listIssues("owner", "repo", 2);
  }

  @Test
  void testGetIssue() {
    IssueDetail mockIssue =
        new IssueDetail(
            new IssueBasic(1, "Test Issue", "open", "testuser", List.of("Feature", "Bug")),
            new IssueStale(4, false),
            "This is a test issue body",
            1,
            List.of(new IssueComment("testuser", "This is a test issue comment")));

    Mockito.when(githubService.getIssue("owner", "repo", 10)).thenReturn(mockIssue);

    IssueDetail issue = tools.getIssue("owner", "repo", 10);

    assertEquals("Feature", issue.issueBasicDetails().labels().get(0));

    Mockito.verify(githubService).getIssue("owner", "repo", 10);
  }

  @Test
  void testListPullRequests() {
    List<PullRequestSummary> mockPullRequests =
        List.of(
            new PullRequestSummary(
                new PullRequestBasic(1, "Test PR", "open", "testuser", List.of("Enhancement")),
                new PullRequestBranch("main", "feature-branch"),
                new PullRequestStale(false, true, false, 3, 2)));

    Mockito.when(githubService.listPullRequests("owner", "repo", 1, 30))
        .thenReturn(mockPullRequests);

    List<PullRequestSummary> result = tools.listPullRequests("owner", "repo", null, null);

    assertEquals(1, result.size());
    assertEquals("Test PR", result.get(0).pullRequestBasicDetails().title());

    Mockito.verify(githubService).listPullRequests("owner", "repo", 1, 30);
  }

  @Test
  void testGetPullRequest() {
    PullRequestDetail mockPullRequest =
        new PullRequestDetail(
            new PullRequestBasic(1, "Test PR", "open", "testuser", List.of("Enhancement")),
            new PullRequestBranch("main", "feature-branch"),
            new PullRequestStale(false, true, false, 3, 2),
            new PullRequestSize(100, 20, 2, 5, true),
            "The is a test pull request body",
            1,
            List.of(
                new PullRequestComment(
                    "testuser", "This is a test pull request comment", "2024-01-01T00:00:00Z")));

    Mockito.when(githubService.getPullRequest("owner", "repo", 10)).thenReturn(mockPullRequest);

    PullRequestDetail result = tools.getPullRequest("owner", "repo", 10);

    assertEquals("Test PR", result.pullRequestBasicDetails().title());
    assertEquals("feature-branch", result.pullRequestBranchDetails().baseBranch());
    assertEquals(3, result.pullRequestStaleDetails().daysOpen());
    assertTrue(result.pullRequestSizeDetails().isLarge());

    Mockito.verify(githubService).getPullRequest("owner", "repo", 10);
  }
}
