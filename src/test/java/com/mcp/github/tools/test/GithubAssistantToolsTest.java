package com.mcp.github.tools.test;

import com.mcp.github.models.Issue;
import com.mcp.github.services.GithubService;
import com.mcp.github.tools.GithubAssistantTools;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import tools.jackson.databind.JsonNode;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

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
        List<Issue> mockIssues = List.of(
                new Issue(1, "Test Issue", "open", "testuser", "2024-01-01T00:00:00Z", 10)
        );

        Mockito.when(githubService.listIssues("owner", "repo"))
                .thenReturn(mockIssues);

        List<Issue> result = tools.listIssues("owner", "repo");

        assertEquals(1, result.size());
        assertEquals("Test Issue", result.get(0).title());

        Mockito.verify(githubService).listIssues("owner", "repo");
    }
}
