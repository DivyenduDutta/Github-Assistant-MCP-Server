package com.mcp.github.client.tests;

import com.mcp.github.client.GithubHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.lang.reflect.Field;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

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

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> githubHttpClient.getIssues("owner", "repo")
        );

        assertTrue(ex.getMessage().contains("GitHub API error"));
    }
}
