package com.example.springaimcp.service.mcp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;

/**
 * Client for communicating with GitHub MCP (Model Context Protocol) server
 * 
 * This client handles communication with a GitHub MCP server that provides
 * GitHub API functionality through the MCP protocol.
 */
@Service
public class GitHubMcpClient {

    private static final Logger logger = LoggerFactory.getLogger(GitHubMcpClient.class);
    
    private final WebClient mcpWebClient;
    private final ObjectMapper objectMapper;

    public GitHubMcpClient(@Qualifier("mcpWebClient") WebClient mcpWebClient) {
        this.mcpWebClient = mcpWebClient;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Execute a GitHub query through the MCP server
     * 
     * @param query The natural language query about GitHub
     * @return Map containing the MCP server response
     */
    public Map<String, Object> executeGitHubQuery(String query) {
        logger.info("Executing GitHub MCP query: {}", query);
        
        try {
            // Build MCP request payload
            Map<String, Object> mcpRequest = buildMcpRequest(query);
            
            // Call MCP server
            Mono<Map> responseMono = mcpWebClient
                    .post()
                    .uri("/mcp/github")
                    .bodyValue(mcpRequest)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(30));
            
            Map<String, Object> response = responseMono.block();
            
            if (response != null) {
                logger.info("MCP query executed successfully");
                return response;
            } else {
                logger.warn("MCP server returned null response");
                return createErrorResponse("MCP server returned empty response");
            }
            
        } catch (Exception e) {
            logger.error("Error executing GitHub MCP query: {}", e.getMessage(), e);
            return createErrorResponse("Error communicating with MCP server: " + e.getMessage());
        }
    }

    /**
     * Get available GitHub tools from MCP server
     */
    public List<Map<String, Object>> getAvailableTools() {
        logger.info("Fetching available GitHub MCP tools");
        
        try {
            Mono<List> responseMono = mcpWebClient
                    .get()
                    .uri("/mcp/tools")
                    .retrieve()
                    .bodyToMono(List.class)
                    .timeout(Duration.ofSeconds(10));
            
            List<Map<String, Object>> tools = responseMono.block();
            
            if (tools != null) {
                logger.info("Retrieved {} GitHub MCP tools", tools.size());
                return tools;
            } else {
                logger.warn("No tools returned from MCP server");
                return getDefaultTools();
            }
            
        } catch (Exception e) {
            logger.error("Error fetching MCP tools: {}", e.getMessage(), e);
            return getDefaultTools();
        }
    }

    /**
     * Build MCP request payload
     */
    private Map<String, Object> buildMcpRequest(String query) {
        Map<String, Object> request = new HashMap<>();
        request.put("method", "query");
        request.put("query", query);
        request.put("timestamp", System.currentTimeMillis());
        
        // Add query analysis to determine required tools
        Map<String, Object> params = new HashMap<>();
        params.put("analyze_intent", true);
        params.put("max_results", 10);
        
        // Determine GitHub operation type based on query
        String lowerQuery = query.toLowerCase();
        if (lowerQuery.contains("repository") || lowerQuery.contains("repo")) {
            params.put("operation", "repository");
        } else if (lowerQuery.contains("issue")) {
            params.put("operation", "issues");
        } else if (lowerQuery.contains("pull request") || lowerQuery.contains("pr")) {
            params.put("operation", "pull_requests");
        } else {
            params.put("operation", "general");
        }
        
        request.put("parameters", params);
        return request;
    }

    /**
     * Create error response
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", true);
        errorResponse.put("message", message);
        errorResponse.put("status", "failed");
        errorResponse.put("timestamp", System.currentTimeMillis());
        return errorResponse;
    }

    /**
     * Get default tools when MCP server is unavailable
     */
    private List<Map<String, Object>> getDefaultTools() {
        List<Map<String, Object>> defaultTools = new ArrayList<>();
        
        Map<String, Object> repoTool = new HashMap<>();
        repoTool.put("name", "github_repository");
        repoTool.put("description", "Get repository information");
        repoTool.put("available", false);
        repoTool.put("reason", "MCP server unavailable");
        defaultTools.add(repoTool);
        
        Map<String, Object> issuesTool = new HashMap<>();
        issuesTool.put("name", "github_issues");
        issuesTool.put("description", "Manage GitHub issues");
        issuesTool.put("available", false);
        issuesTool.put("reason", "MCP server unavailable");
        defaultTools.add(issuesTool);
        
        return defaultTools;
    }
}
