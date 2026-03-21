package com.mcp.github.services;

import com.mcp.github.client.GithubHttpClient;
import com.mcp.github.models.Issue;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class GithubService {

    private final GithubHttpClient client;

    public GithubService(GithubHttpClient client) {
        this.client = client;
    }

    public List<Issue> listIssues(String owner, String repo) {
        JsonNode response = client.getIssues(owner, repo);

        List<Issue> issues = new ArrayList<>();

        for (JsonNode node : response) {

            // Skip pull requests (GitHub mixes them in)
            if (node.has("pull_request")) {
                continue;
            }

            int number = node.get("number").asInt();
            String title = node.get("title").asString();
            String state = node.get("state").asString();
            String author = node.get("user").get("login").asString();
            String createdAt = node.get("created_at").asString();

            long daysOpen = ChronoUnit.DAYS.between(
                    OffsetDateTime.parse(createdAt),
                    OffsetDateTime.now()
            );

            issues.add(new Issue(
                    number,
                    title,
                    state,
                    author,
                    createdAt,
                    (int) daysOpen
            ));
        }

        return issues;
    }
}
