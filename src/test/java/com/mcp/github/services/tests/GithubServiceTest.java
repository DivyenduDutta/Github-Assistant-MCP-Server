package com.mcp.github.services.tests;

import com.mcp.github.client.GithubHttpClient;
import com.mcp.github.models.Issue;
import com.mcp.github.services.GithubService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;


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
    public void testListIssues(){
        String mockResponse = """
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
        
        Mockito.doReturn(res)
                .when(githubHttpClient)
                .getIssues(any(String.class), any(String.class));

        List<Issue> issues = githubService.listIssues("owner", "repo");
        assertEquals(1, issues.size());
        Issue issue = issues.get(0);
        assertEquals("Test Issue", issue.title());
        
        Mockito.verify(githubHttpClient).getIssues(any(String.class), any(String.class));
    }
}
