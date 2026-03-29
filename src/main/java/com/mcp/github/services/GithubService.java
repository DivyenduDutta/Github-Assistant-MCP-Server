package com.mcp.github.services;

import com.mcp.github.client.GithubHttpClient;
import com.mcp.github.models.Issue;
import com.mcp.github.models.IssueComment;
import com.mcp.github.models.IssueDetail;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

/**
 * Service layer that interacts with the GithubHttpClient to fetch and process data from GitHub's
 * API. It transforms raw JSON responses into structured Issue objects for easier consumption by
 * other parts of the application.
 */
@Service
public class GithubService {

  private static final int STALE_DAYS_THRESHOLD = 30;
  private static final int MAX_COMMENTS_FOR_CONTEXT = 5;
  private static final int DEFAULT_ISSUE_LIMIT = 5;

  private final GithubHttpClient client;

  public GithubService(GithubHttpClient client) {
    this.client = client;
  }

  /**
   * Fetches a list of open issues from a specified GitHub repository and transforms them into
   * `Issue` objects. This method provides a default limit of issues to ensure that the assistant
   * has a manageable number of issues to work with for LLM context, while still giving a
   * representative sample of the repository's current issues.
   *
   * @param owner The owner of the repository (e.g., "octocat").
   * @param repo The name of the repository (e.g., "Hello-World").
   * @return A list of Issue objects representing the open issues in the repository.
   */
  public List<Issue> listIssues(String owner, String repo) {
    return this.listIssues(owner, repo, DEFAULT_ISSUE_LIMIT);
  }

  /**
   * Fetches a list of open issues from a specified GitHub repository and transforms them into
   * `Issue` objects. This method allows limiting the number of issues returned for better LLM
   * context management.
   *
   * @param owner The owner of the repository (e.g., "octocat").
   * @param repo The name of the repository (e.g., "Hello-World").
   * @param numberOfIssues The maximum number of issues to return.
   * @return A list of Issue objects representing the open issues in the repository.
   */
  public List<Issue> listIssues(String owner, String repo, int numberOfIssues) {
    JsonNode response = client.getIssues(owner, repo);

    List<Issue> issues = new ArrayList<>();

    for (JsonNode node : response) {

      // Limit the number of issues returned for LLM context
      if (issues.size() == numberOfIssues) {
        break;
      }

      // Skip pull requests (GitHub mixes them in)
      if (node.has("pull_request")) {
        continue;
      }

      int number = node.get("number").asInt();
      String title = node.get("title").asString();
      String state = node.get("state").asString();
      String author = node.get("user").get("login").asString();
      String createdAt = node.get("created_at").asString();

      long daysOpen =
          ChronoUnit.DAYS.between(OffsetDateTime.parse(createdAt), OffsetDateTime.now());

      issues.add(new Issue(number, title, state, author, createdAt, (int) daysOpen));
    }

    return issues;
  }

  /**
   * Fetches detailed information about a specific issue from a GitHub repository, including its
   * title, description, labels, recent comments, and computed metadata like days open and
   * staleness. It ensures that the requested item is an issue and not a pull request.
   *
   * @param owner The owner of the repository (e.g., "octocat").
   * @param repo The name of the repository (e.g., "Hello-World").
   * @param issueNumber The number of the issue to fetch details for.
   * @return An IssueDetail object containing comprehensive information about the specified issue.
   */
  public IssueDetail getIssue(String owner, String repo, int issueNumber) {

    JsonNode issueNode = client.getIssue(owner, repo, issueNumber);

    // Skip pull requests (GitHub mixes them in)
    if (issueNode.has("pull_request")) {
      throw new RuntimeException("Requested item is a Pull Request, not an Issue");
    }

    JsonNode commentsNode = client.getIssueComments(owner, repo, issueNumber);

    // Basic fields
    int number = issueNode.get("number").asInt();
    String title = issueNode.get("title").asString();
    String body = issueNode.get("body").asString("");
    String state = issueNode.get("state").asString();
    String author = issueNode.get("user").get("login").asString();

    // Labels
    List<String> labels = new ArrayList<>();
    for (JsonNode labelNode : issueNode.get("labels")) {
      labels.add(labelNode.get("name").asString());
    }

    // Comments
    List<IssueComment> comments = new ArrayList<>();
    for (JsonNode commentNode : commentsNode) {
      String commentAuthor = commentNode.get("user").get("login").asString();
      String commentBody = commentNode.get("body").asString("");
      comments.add(new IssueComment(commentAuthor, commentBody));
    }

    // Limit to first 5 comments for LLM context
    List<IssueComment> limitedComments = comments.stream().limit(MAX_COMMENTS_FOR_CONTEXT).toList();

    int commentCount = limitedComments.size();

    // Time calculations
    String createdAt = issueNode.get("created_at").asString();

    long daysOpen = ChronoUnit.DAYS.between(OffsetDateTime.parse(createdAt), OffsetDateTime.now());

    // Simple "stale" logic
    boolean isStale = daysOpen > STALE_DAYS_THRESHOLD && commentCount == 0;

    return new IssueDetail(
        number,
        title,
        body,
        state,
        author,
        labels,
        commentCount,
        limitedComments,
        (int) daysOpen,
        isStale);
  }
}
