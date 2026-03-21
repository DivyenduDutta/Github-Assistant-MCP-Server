package com.mcp.github.tools;

import com.mcp.github.models.Issue;
import com.mcp.github.services.GithubService;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GithubAssistantTools {

    private final GithubService githubService;

    public GithubAssistantTools(GithubService gitHubService) {
        this.githubService = gitHubService;
    }

    @McpTool(name = "list_issues", description = "Retrieve a list of open issues from a GitHub repository. Useful for understanding current work and pending bugs.")
    public List<Issue> listIssues(
            @ToolParam(description = "Repository owner, e.g. 'octocat'")
            String owner,

            @ToolParam(description = "Repository name, e.g. 'Hello-World'")
            String repo
    ) {
        return githubService.listIssues(owner, repo);
    }
}
