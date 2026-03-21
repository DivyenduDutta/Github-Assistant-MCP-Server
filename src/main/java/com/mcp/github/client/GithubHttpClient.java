package com.mcp.github.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class GithubHttpClient {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Value("${github.token}")
    private String token;

    public GithubHttpClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public JsonNode getIssues(String owner, String repo) {
        try {
            String url = String.format(
                    "https://api.github.com/repos/%s/%s/issues",
                    owner,
                    repo
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + token)
                    .header("Accept", "application/vnd.github+json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() != 200) {
                throw new RuntimeException(
                        "GitHub API error: " + response.statusCode() + " - " + response.body()
                );
            }

            return objectMapper.readTree(response.body());

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch issues from GitHub", e);
        }
    }
}
