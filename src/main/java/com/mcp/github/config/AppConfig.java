package com.mcp.github.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;

import java.net.http.HttpClient;

/**
 * Configuration class for the application. These beans are used throughout
 * the application for making HTTP requests to the GitHub API and for parsing JSON responses. By defining them as beans,
 * we can easily manage their lifecycle and dependencies using Spring's dependency injection framework.
 */
@Configuration
public class AppConfig {
    @Bean
    public HttpClient httpClient() {
        return HttpClient.newHttpClient();
    }
}
