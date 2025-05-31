package com.example.springaimcp.config;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration class for Spring AI and related components
 */
@Configuration
public class SpringAiConfig {

    @Value("${spring.ai.openai.api-key}")
    private String openAiApiKey;

    @Value("${spring.ai.openai.base-url:https://api.openai.com}")
    private String openAiBaseUrl;

    @Value("${app.github.mcp.server.url:http://localhost:3000}")
    private String mcpServerUrl;

    /**
     * Configure OpenAI Chat Model for LLM interactions
     */
    @Bean
    public OpenAiChatModel openAiChatModel() {
        var openAiApi = new OpenAiApi(openAiBaseUrl, openAiApiKey);
        return new OpenAiChatModel(openAiApi);
    }

    /**
     * WebClient for GitHub MCP server communication
     */
    @Bean("mcpWebClient")
    public WebClient mcpWebClient() {
        return WebClient.builder()
                .baseUrl(mcpServerUrl)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .build();
    }

    /**
     * General purpose WebClient
     */
    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }
}
