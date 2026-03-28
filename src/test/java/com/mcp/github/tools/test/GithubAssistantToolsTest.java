package com.mcp.github.tools.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.mcp.github.models.Issue;
import com.mcp.github.models.IssueComment;
import com.mcp.github.models.IssueDetail;
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
}
