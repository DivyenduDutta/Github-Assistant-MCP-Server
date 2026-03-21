package com.mcp.github.tools;

import com.mcp.github.models.Issue;
import com.mcp.github.services.GithubService;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * A collection of tools for the GitHub Assistant, providing functionalities to interact with GitHub repositories.
 * This class uses the GithubService to fetch data from GitHub and exposes it as tools that can be used by the assistant.
 */
@Component
public class GithubAssistantTools {

    private final GithubService githubService;

    public GithubAssistantTools(GithubService gitHubService) {
        this.githubService = gitHubService;
    }
    
    /**
     * Retrieves a list of open issues from a specified GitHub repository. This tool is useful for understanding
     * the current work and pending bugs in a repository. It takes the repository owner and name as parameters
     * and returns a list of Issue objects representing the open issues.
     *
     * @param owner The owner of the repository (e.g., "octocat").
     * @param repo  The name of the repository (e.g., "Hello-World").
     * @return A list of Issue objects representing the open issues in the repository.
     */
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
