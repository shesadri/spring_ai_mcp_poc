package com.example.springaimcp.service;

import com.example.springaimcp.model.PromptRequest;
import com.example.springaimcp.model.PromptResponse;
import com.example.springaimcp.service.mcp.GitHubMcpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Service class that integrates Spring AI with GitHub MCP server
 */
@Service
public class AiMcpService {

    private static final Logger logger = LoggerFactory.getLogger(AiMcpService.class);

    private final ChatModel chatModel;
    private final GitHubMcpClient gitHubMcpClient;

    @Autowired
    public AiMcpService(ChatModel chatModel, GitHubMcpClient gitHubMcpClient) {
        this.chatModel = chatModel;
        this.gitHubMcpClient = gitHubMcpClient;
    }

    /**
     * Process a user prompt using Spring AI with GitHub MCP integration
     * 
     * This method:
     * 1. Analyzes the prompt to determine if GitHub tools are needed
     * 2. Calls the LLM with available GitHub MCP functions
     * 3. Executes any function calls through the MCP server
     * 4. Returns the final AI response
     */
    public PromptResponse processPrompt(PromptRequest request) {
        logger.info("Processing prompt with AI and MCP integration");
        
        try {
            // Enhance the prompt with available GitHub tools context
            String enhancedPrompt = buildEnhancedPrompt(request.getPrompt());
            
            // Configure chat options with GitHub MCP functions
            OpenAiChatOptions chatOptions = OpenAiChatOptions.builder()
                    .withModel("gpt-4")
                    .withTemperature(0.7f)
                    .withMaxTokens(1000)
                    .build();

            // Create prompt with enhanced context
            Prompt prompt = new Prompt(enhancedPrompt, chatOptions);

            // Call the LLM
            var response = chatModel.call(prompt);
            String aiResponse = response.getResult().getOutput().getContent();

            // Check if the AI response indicates need for GitHub tools
            Map<String, Object> mcpResult = null;
            boolean usedMcpTools = false;
            
            if (requiresGitHubTools(request.getPrompt())) {
                logger.info("Executing GitHub MCP tools for enhanced response");
                mcpResult = gitHubMcpClient.executeGitHubQuery(request.getPrompt());
                usedMcpTools = true;
                
                // Enhance AI response with MCP data if available
                if (mcpResult != null && !mcpResult.isEmpty()) {
                    aiResponse = enhanceResponseWithMcpData(aiResponse, mcpResult);
                }
            }

            return new PromptResponse(aiResponse, mcpResult, usedMcpTools);
            
        } catch (Exception e) {
            logger.error("Error in AI MCP processing: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process prompt with AI MCP integration", e);
        }
    }

    /**
     * Get available GitHub MCP tools
     */
    public List<Map<String, Object>> getAvailableTools() {
        return gitHubMcpClient.getAvailableTools();
    }

    /**
     * Build enhanced prompt with GitHub MCP context
     */
    private String buildEnhancedPrompt(String originalPrompt) {
        StringBuilder enhanced = new StringBuilder();
        enhanced.append("You are an AI assistant with access to GitHub tools through MCP (Model Context Protocol).\n");
        enhanced.append("You can help with GitHub-related queries including repository information, issues, pull requests, and more.\n\n");
        enhanced.append("Available GitHub capabilities:\n");
        enhanced.append("- Repository information and statistics\n");
        enhanced.append("- Issue management and tracking\n");
        enhanced.append("- Pull request analysis\n");
        enhanced.append("- Code search and file operations\n");
        enhanced.append("- Branch and commit information\n\n");
        enhanced.append("User prompt: ").append(originalPrompt);
        
        return enhanced.toString();
    }

    /**
     * Determine if the prompt requires GitHub tools
     */
    private boolean requiresGitHubTools(String prompt) {
        String lowerPrompt = prompt.toLowerCase();
        return lowerPrompt.contains("github") || 
               lowerPrompt.contains("repository") || 
               lowerPrompt.contains("repo") ||
               lowerPrompt.contains("issue") ||
               lowerPrompt.contains("pull request") ||
               lowerPrompt.contains("commit") ||
               lowerPrompt.contains("branch");
    }

    /**
     * Enhance AI response with MCP data
     */
    private String enhanceResponseWithMcpData(String aiResponse, Map<String, Object> mcpResult) {
        StringBuilder enhanced = new StringBuilder(aiResponse);
        enhanced.append("\n\n**GitHub Data from MCP Server:**\n");
        
        if (mcpResult.containsKey("repositories")) {
            enhanced.append("\n📁 **Repositories Found:**\n");
            enhanced.append(mcpResult.get("repositories").toString());
        }
        
        if (mcpResult.containsKey("issues")) {
            enhanced.append("\n🎯 **Issues:**\n");
            enhanced.append(mcpResult.get("issues").toString());
        }
        
        if (mcpResult.containsKey("status")) {
            enhanced.append("\n📊 **Status:** ").append(mcpResult.get("status"));
        }
        
        return enhanced.toString();
    }
}
