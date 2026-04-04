package com.mcp.github.services;

import com.mcp.github.client.GithubHttpClient;
import com.mcp.github.models.issue.Issue;
import com.mcp.github.models.issue.IssueComment;
import com.mcp.github.models.issue.IssueDetail;
import com.mcp.github.models.pr.PullRequestComment;
import com.mcp.github.models.pr.PullRequestDetail;
import com.mcp.github.models.pr.PullRequestSummary;
import java.time.Clock;
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
  private static final int MAX_ISSUE_COMMENTS_FOR_CONTEXT = 5;
  private static final int MAX_PR_COMMENTS_FOR_CONTEXT = 5;
  private static final int DEFAULT_ISSUE_LIMIT = 5;
  private static final int STALE_DAYS_FOR_PR = 14;
  private static final int PR_LARGE_THRESHOLD_1 = 500;
  private static final int PR_LARGE_THRESHOLD_2 = 10;

  private final GithubHttpClient client;
  private final Clock clock;

  public GithubService(GithubHttpClient client, Clock clock) {
    this.client = client;
    this.clock = clock;
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
          ChronoUnit.DAYS.between(OffsetDateTime.parse(createdAt), OffsetDateTime.now(clock));

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
    List<IssueComment> limitedComments =
        comments.stream().limit(MAX_ISSUE_COMMENTS_FOR_CONTEXT).toList();

    int commentCount = limitedComments.size();

    // Time calculations
    String createdAt = issueNode.get("created_at").asString();

    long daysOpen =
        ChronoUnit.DAYS.between(OffsetDateTime.parse(createdAt), OffsetDateTime.now(clock));

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

  /**
   * Fetches a list of pull requests from a specified GitHub repository and transforms them into
   * `PullRequestSummary` objects. This method provides detailed information about each pull
   * request, including its number, title, state, author, branch info, labels, comment count,
   * time-based metadata, and staleness status. It allows for pagination to query large
   * repositories.
   *
   * @param owner The owner of the repository (e.g., "octocat").
   * @param repo The name of the repository (e.g., "Hello-World").
   * @param page The page number for pagination (starting from 1).
   * @param perPage The number of pull requests to return per page.
   * @return A list of PullRequestSummary objects representing the pull requests in the repository.
   */
  public List<PullRequestSummary> listPullRequests(
      String owner, String repo, int page, int perPage) {

    JsonNode prArray = client.getPullRequests(owner, repo, page, perPage);

    List<PullRequestSummary> result = new ArrayList<>();

    for (JsonNode prNode : prArray) {

      // Basic fields
      int number = prNode.get("number").asInt();
      String title = prNode.get("title").asString();
      String state = prNode.get("state").asString();
      String author = prNode.get("user").get("login").asString();

      // Branch info
      String headBranch = prNode.get("head").get("ref").asString();
      String baseBranch = prNode.get("base").get("ref").asString();

      // Labels
      List<String> labels = new ArrayList<>();
      if (prNode.has("labels")) {
        for (JsonNode labelNode : prNode.get("labels")) {
          labels.add(labelNode.get("name").asString());
        }
      }

      // Comments - defensive coding using .path() because "comments" might be missing in some
      // versions of the API
      int commentCount = prNode.path("comments").asInt(0);

      // Time fields
      String createdAt = prNode.get("created_at").asString();
      String updatedAt = prNode.get("updated_at").asString();

      int daysOpen =
          (int) ChronoUnit.DAYS.between(OffsetDateTime.parse(createdAt), OffsetDateTime.now(clock));

      int daysSinceLastUpdate =
          (int) ChronoUnit.DAYS.between(OffsetDateTime.parse(updatedAt), OffsetDateTime.now(clock));

      // PR state
      boolean isDraft = prNode.get("draft").asBoolean();
      boolean isMerged = prNode.get("merged_at") != null && !prNode.get("merged_at").isNull();

      // Staleness logic (better than daysOpen)
      boolean isStale = !isMerged && daysSinceLastUpdate > STALE_DAYS_FOR_PR;

      PullRequestSummary pr =
          new PullRequestSummary(
              number,
              title,
              state,
              author,
              headBranch,
              baseBranch,
              labels,
              commentCount,
              daysOpen,
              daysSinceLastUpdate,
              isDraft,
              isMerged,
              isStale);

      result.add(pr);
    }
    return result;
  }

  public PullRequestDetail getPullRequest(String owner, String repo, int pullRequestNumber) {

    JsonNode pullRequestNode = client.getPullRequest(owner, repo, pullRequestNumber);

    // This looks confusing but GitHub's API returns pull requests as a special type of issue, so we
    // need to fetch
    // the comments using the issue comments endpoint
    JsonNode pullRequestCommentsNode = client.getIssueComments(owner, repo, pullRequestNumber);

    // Basic fields
    int number = pullRequestNode.get("number").asInt();
    String title = pullRequestNode.get("title").asString();
    String body = pullRequestNode.get("body").asString("");
    String state = pullRequestNode.get("state").asString();
    String author = pullRequestNode.get("user").get("login").asString();

    // Branch info
    String headBranch = pullRequestNode.get("head").get("ref").asString();
    String baseBranch = pullRequestNode.get("base").get("ref").asString();

    // Labels
    List<String> labels = new ArrayList<>();
    for (JsonNode labelNode : pullRequestNode.get("labels")) {
      labels.add(labelNode.get("name").asString());
    }

    // Comments
    List<PullRequestComment> comments = new ArrayList<>();
    for (JsonNode pullRequestCommentNode : pullRequestCommentsNode) {
      String commentAuthor = pullRequestCommentNode.get("user").get("login").asString();
      String commentBody = pullRequestNode.get("body").asString("");
      String commentCreatedAt = pullRequestNode.get("created_at").asString();
      comments.add(new PullRequestComment(commentAuthor, commentBody, commentCreatedAt));
    }

    // Limit to first 5 comments for LLM context
    List<PullRequestComment> limitedComments =
        comments.stream().limit(MAX_PR_COMMENTS_FOR_CONTEXT).toList();

    int commentCount = limitedComments.size();

    // Time fields
    String createdAt = pullRequestNode.get("created_at").asString();
    String updatedAt = pullRequestNode.get("updated_at").asString();

    int daysOpen =
        (int) ChronoUnit.DAYS.between(OffsetDateTime.parse(createdAt), OffsetDateTime.now(clock));

    int daysSinceLastUpdate =
        (int) ChronoUnit.DAYS.between(OffsetDateTime.parse(updatedAt), OffsetDateTime.now(clock));

    // PR state
    boolean isDraft = pullRequestNode.get("draft").asBoolean();
    boolean isMerged =
        pullRequestNode.get("merged_at") != null && !pullRequestNode.get("merged_at").isNull();

    // Staleness logic (better than daysOpen)
    boolean isStale = !isMerged && daysSinceLastUpdate > STALE_DAYS_FOR_PR;

    // PR size metrics
    int additions = pullRequestNode.get("additions").asInt(0);
    int deletions = pullRequestNode.get("deletions").asInt(0);
    int commits = pullRequestNode.get("commits").asInt(0);
    int changedFiles = pullRequestNode.get("changed_files").asInt(0);
    boolean isLarge =
        (additions + deletions) > PR_LARGE_THRESHOLD_1
            || (commits + changedFiles) > PR_LARGE_THRESHOLD_2;

    return new PullRequestDetail(
        number,
        title,
        body,
        state,
        author,
        headBranch,
        baseBranch,
        labels,
        isDraft,
        isMerged,
        isStale,
        daysOpen,
        daysSinceLastUpdate,
        additions,
        deletions,
        commits,
        changedFiles,
        isLarge,
        commentCount,
        limitedComments);
  }
}
