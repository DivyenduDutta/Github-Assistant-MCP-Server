package com.mcp.github.tools;

import com.mcp.github.models.Issue;
import com.mcp.github.models.IssueDetail;
import com.mcp.github.services.GithubService;
import java.util.List;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/**
 * A collection of tools for the GitHub Assistant, providing functionalities to interact with GitHub
 * repositories. This class uses the GithubService to fetch data from GitHub and exposes it as tools
 * that can be used by the assistant.
 */
@Component
public class GithubAssistantTools {

  private final GithubService githubService;

  public GithubAssistantTools(GithubService gitHubService) {
    this.githubService = gitHubService;
  }

  /**
   * Retrieves a list of open issues from a specified GitHub repository. This tool is useful for
   * understanding the current work and pending bugs in a repository. It takes the repository owner
   * and name as parameters and returns a list of Issue objects representing the open issues.
   *
   * @param owner The owner of the repository (e.g., "octocat").
   * @param repo The name of the repository (e.g., "Hello-World").
   * @param numberOfIssues The maximum number of issues to return for better LLM context management.
   * @return A list of Issue objects representing the open issues in the repository.
   */
  @McpTool(
      name = "list_issues_limited",
      description =
          "Retrieve a list of open issues from a GitHub repository. Useful for understanding current work and pending bugs.")
  public List<Issue> listIssues(
      @ToolParam(description = "Repository owner, e.g. 'octocat'") String owner,
      @ToolParam(description = "Repository name, e.g. 'Hello-World'") String repo,
      @ToolParam(
              description = "Maximum number of issues to return for better LLM context management")
          int numberOfIssues) {
    return githubService.listIssues(owner, repo, numberOfIssues);
  }

  /**
   * Retrieves a default list of open issues from a specified GitHub repository. This tool is useful
   * for understanding the current work and pending bugs in a repository. It takes the repository
   * owner and name as parameters and returns a list of Issue objects representing the open issues,
   * using a default limit to ensure manageable LLM context.
   *
   * @param owner The owner of the repository (e.g., "octocat").
   * @param repo The name of the repository (e.g., "Hello-World").
   * @return A list of Issue objects representing the open issues in the repository, limited to a
   *     default number for better LLM context management.
   */
  @McpTool(
      name = "list_issues_default",
      description =
          "Retrieve a list of open issues from a GitHub repository. Useful for understanding current work and pending "
              + "bugs. This version uses a default limit to ensure manageable LLM context.")
  public List<Issue> listIssues(
      @ToolParam(description = "Repository owner, e.g. 'octocat'") String owner,
      @ToolParam(description = "Repository name, e.g. 'Hello-World'") String repo) {
    return githubService.listIssues(owner, repo);
  }

  /**
   * Retrieves detailed information about a specific GitHub issue, including its title, description,
   * labels, recent comments, and computed metadata such as days open and staleness. This tool is
   * useful for gaining a comprehensive understanding of an issue's status and history. It takes the
   * repository owner, name, and issue number as parameters and returns an IssueDetail object
   * containing all relevant information about the issue.
   *
   * @param owner The owner of the repository (e.g., "octocat").
   * @param repo The name of the repository (e.g., "Hello-World").
   * @param issueNumber The number of the issue to fetch details for.
   * @return An IssueDetail object containing comprehensive information about the specified issue.
   */
  @McpTool(
      name = "get_issue",
      description =
          "Get full details of a GitHub issue including title, description, labels, recent comments, and computed metadata like days open and staleness.")
  public IssueDetail getIssue(
      @ToolParam(description = "Repository owner, e.g. 'octocat'") String owner,
      @ToolParam(description = "Repository name, e.g. 'Hello-World'") String repo,
      @ToolParam(description = "Issue number (not pull request number), e.g. 123")
          int issueNumber) {
    return githubService.getIssue(owner, repo, issueNumber);
  }
}
