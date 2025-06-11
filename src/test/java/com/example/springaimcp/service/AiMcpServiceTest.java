package com.example.springaimcp.service;

import com.example.springaimcp.model.PromptRequest;
import com.example.springaimcp.model.PromptResponse;
import com.example.springaimcp.service.mcp.GitHubMcpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.openai.api.OpenAiApi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive test suite for AiMcpService
 * Tests all public methods and edge cases with proper mocking
 */
@ExtendWith(MockitoExtension.class)
class AiMcpServiceTest {

    @Mock
    private ChatModel chatModel;

    @Mock
    private GitHubMcpClient gitHubMcpClient;

    @InjectMocks
    private AiMcpService aiMcpService;

    private PromptRequest promptRequest;
    private ChatResponse mockChatResponse;
    private Generation mockGeneration;

    @BeforeEach
    void setUp() {
        promptRequest = new PromptRequest();
        
        // Setup mock objects
        mockGeneration = mock(Generation.class);
        mockChatResponse = mock(ChatResponse.class);
        
        // Setup default mock behavior
        when(mockChatResponse.getResult()).thenReturn(mockGeneration);
        when(mockGeneration.getOutput()).thenReturn(mock(OpenAiApi.ChatCompletion.Choice.Message.class));
        when(mockGeneration.getOutput().getContent()).thenReturn("AI response");
    }

    @Test
    void testProcessPrompt_WithNonGitHubPrompt_ShouldReturnBasicResponse() {
        // Arrange
        promptRequest.setPrompt("What is the weather today?");
        when(chatModel.call(any())).thenReturn(mockChatResponse);
        
        // Act
        PromptResponse response = aiMcpService.processPrompt(promptRequest);
        
        // Assert
        assertNotNull(response);
        assertEquals("AI response", response.getResponse());
        assertFalse(response.isUsedMcpTools());
        assertNull(response.getMcpData());
        verify(gitHubMcpClient, never()).executeGitHubQuery(anyString());
    }

    @Test
    void testProcessPrompt_WithGitHubKeyword_ShouldUseMcpTools() {
        // Arrange
        promptRequest.setPrompt("Show me GitHub repositories for spring boot");
        Map<String, Object> mcpResult = new HashMap<>();
        mcpResult.put("repositories", List.of("spring-boot/spring-boot"));
        mcpResult.put("status", "success");
        
        when(chatModel.call(any())).thenReturn(mockChatResponse);
        when(gitHubMcpClient.executeGitHubQuery(anyString())).thenReturn(mcpResult);
        
        // Act
        PromptResponse response = aiMcpService.processPrompt(promptRequest);
        
        // Assert
        assertNotNull(response);
        assertTrue(response.isUsedMcpTools());
        assertNotNull(response.getMcpData());
        assertEquals(mcpResult, response.getMcpData());
        assertTrue(response.getResponse().contains("GitHub Data from MCP Server"));
        verify(gitHubMcpClient).executeGitHubQuery("Show me GitHub repositories for spring boot");
    }

    @Test
    void testProcessPrompt_WithRepositoryKeyword_ShouldUseMcpTools() {
        // Arrange
        promptRequest.setPrompt("Find repository information for my project");
        Map<String, Object> mcpResult = new HashMap<>();
        mcpResult.put("repositories", List.of("user/project"));
        
        when(chatModel.call(any())).thenReturn(mockChatResponse);
        when(gitHubMcpClient.executeGitHubQuery(anyString())).thenReturn(mcpResult);
        
        // Act
        PromptResponse response = aiMcpService.processPrompt(promptRequest);
        
        // Assert
        assertTrue(response.isUsedMcpTools());
        assertNotNull(response.getMcpData());
        verify(gitHubMcpClient).executeGitHubQuery("Find repository information for my project");
    }

    @Test
    void testProcessPrompt_WithIssueKeyword_ShouldUseMcpTools() {
        // Arrange
        promptRequest.setPrompt("List all open issues in the project");
        Map<String, Object> mcpResult = new HashMap<>();
        mcpResult.put("issues", List.of("Issue #1", "Issue #2"));
        
        when(chatModel.call(any())).thenReturn(mockChatResponse);
        when(gitHubMcpClient.executeGitHubQuery(anyString())).thenReturn(mcpResult);
        
        // Act
        PromptResponse response = aiMcpService.processPrompt(promptRequest);
        
        // Assert
        assertTrue(response.isUsedMcpTools());
        assertTrue(response.getResponse().contains("🎯 **Issues:**"));
        verify(gitHubMcpClient).executeGitHubQuery("List all open issues in the project");
    }

    @Test
    void testProcessPrompt_WithPullRequestKeyword_ShouldUseMcpTools() {
        // Arrange
        promptRequest.setPrompt("Show me recent pull request activity");
        Map<String, Object> mcpResult = new HashMap<>();
        mcpResult.put("pull_requests", List.of("PR #1", "PR #2"));
        
        when(chatModel.call(any())).thenReturn(mockChatResponse);
        when(gitHubMcpClient.executeGitHubQuery(anyString())).thenReturn(mcpResult);
        
        // Act
        PromptResponse response = aiMcpService.processPrompt(promptRequest);
        
        // Assert
        assertTrue(response.isUsedMcpTools());
        assertNotNull(response.getMcpData());
        verify(gitHubMcpClient).executeGitHubQuery("Show me recent pull request activity");
    }

    @Test
    void testProcessPrompt_WithCommitKeyword_ShouldUseMcpTools() {
        // Arrange
        promptRequest.setPrompt("Get latest commit information");
        Map<String, Object> mcpResult = new HashMap<>();
        mcpResult.put("commits", List.of("abc123", "def456"));
        
        when(chatModel.call(any())).thenReturn(mockChatResponse);
        when(gitHubMcpClient.executeGitHubQuery(anyString())).thenReturn(mcpResult);
        
        // Act
        PromptResponse response = aiMcpService.processPrompt(promptRequest);
        
        // Assert
        assertTrue(response.isUsedMcpTools());
        verify(gitHubMcpClient).executeGitHubQuery("Get latest commit information");
    }

    @Test
    void testProcessPrompt_WithBranchKeyword_ShouldUseMcpTools() {
        // Arrange
        promptRequest.setPrompt("List all branch names in the repository");
        Map<String, Object> mcpResult = new HashMap<>();
        mcpResult.put("branches", List.of("main", "develop", "feature/test"));
        
        when(chatModel.call(any())).thenReturn(mockChatResponse);
        when(gitHubMcpClient.executeGitHubQuery(anyString())).thenReturn(mcpResult);
        
        // Act
        PromptResponse response = aiMcpService.processPrompt(promptRequest);
        
        // Assert
        assertTrue(response.isUsedMcpTools());
        verify(gitHubMcpClient).executeGitHubQuery("List all branch names in the repository");
    }

    @Test
    void testProcessPrompt_WithEmptyMcpResult_ShouldHandleGracefully() {
        // Arrange
        promptRequest.setPrompt("GitHub repository search");
        Map<String, Object> emptyMcpResult = new HashMap<>();
        
        when(chatModel.call(any())).thenReturn(mockChatResponse);
        when(gitHubMcpClient.executeGitHubQuery(anyString())).thenReturn(emptyMcpResult);
        
        // Act
        PromptResponse response = aiMcpService.processPrompt(promptRequest);
        
        // Assert
        assertTrue(response.isUsedMcpTools());
        assertEquals("AI response", response.getResponse()); // No enhancement due to empty MCP result
        assertEquals(emptyMcpResult, response.getMcpData());
    }

    @Test
    void testProcessPrompt_WithNullMcpResult_ShouldHandleGracefully() {
        // Arrange
        promptRequest.setPrompt("GitHub repository search");
        
        when(chatModel.call(any())).thenReturn(mockChatResponse);
        when(gitHubMcpClient.executeGitHubQuery(anyString())).thenReturn(null);
        
        // Act
        PromptResponse response = aiMcpService.processPrompt(promptRequest);
        
        // Assert
        assertTrue(response.isUsedMcpTools());
        assertEquals("AI response", response.getResponse()); // No enhancement due to null MCP result
        assertNull(response.getMcpData());
    }

    @Test
    void testProcessPrompt_WithChatModelException_ShouldThrowRuntimeException() {
        // Arrange
        promptRequest.setPrompt("Any prompt");
        when(chatModel.call(any())).thenThrow(new RuntimeException("Chat model error"));
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> aiMcpService.processPrompt(promptRequest));
        
        assertTrue(exception.getMessage().contains("Failed to process prompt with AI MCP integration"));
        assertEquals("Chat model error", exception.getCause().getMessage());
    }

    @Test
    void testProcessPrompt_WithMcpClientException_ShouldThrowRuntimeException() {
        // Arrange
        promptRequest.setPrompt("GitHub repository search");
        
        when(chatModel.call(any())).thenReturn(mockChatResponse);
        when(gitHubMcpClient.executeGitHubQuery(anyString()))
            .thenThrow(new RuntimeException("MCP client error"));
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> aiMcpService.processPrompt(promptRequest));
        
        assertTrue(exception.getMessage().contains("Failed to process prompt with AI MCP integration"));
        assertEquals("MCP client error", exception.getCause().getMessage());
    }

    @Test
    void testGetAvailableTools_ShouldReturnToolsFromMcpClient() {
        // Arrange
        List<Map<String, Object>> expectedTools = List.of(
            Map.of("name", "search_repositories", "description", "Search GitHub repositories"),
            Map.of("name", "get_issues", "description", "Get repository issues")
        );
        when(gitHubMcpClient.getAvailableTools()).thenReturn(expectedTools);
        
        // Act
        List<Map<String, Object>> actualTools = aiMcpService.getAvailableTools();
        
        // Assert
        assertEquals(expectedTools, actualTools);
        verify(gitHubMcpClient).getAvailableTools();
    }

    @Test
    void testGetAvailableTools_WithEmptyList_ShouldReturnEmptyList() {
        // Arrange
        when(gitHubMcpClient.getAvailableTools()).thenReturn(List.of());
        
        // Act
        List<Map<String, Object>> actualTools = aiMcpService.getAvailableTools();
        
        // Assert
        assertTrue(actualTools.isEmpty());
        verify(gitHubMcpClient).getAvailableTools();
    }

    @Test
    void testProcessPrompt_WithComplexMcpData_ShouldEnhanceResponseProperly() {
        // Arrange
        promptRequest.setPrompt("GitHub repository analysis");
        Map<String, Object> complexMcpResult = new HashMap<>();
        complexMcpResult.put("repositories", List.of("repo1", "repo2"));
        complexMcpResult.put("issues", List.of("issue1", "issue2"));
        complexMcpResult.put("status", "completed");
        
        when(chatModel.call(any())).thenReturn(mockChatResponse);
        when(gitHubMcpClient.executeGitHubQuery(anyString())).thenReturn(complexMcpResult);
        
        // Act
        PromptResponse response = aiMcpService.processPrompt(promptRequest);
        
        // Assert
        String responseContent = response.getResponse();
        assertTrue(responseContent.contains("GitHub Data from MCP Server"));
        assertTrue(responseContent.contains("📁 **Repositories Found:**"));
        assertTrue(responseContent.contains("🎯 **Issues:**"));
        assertTrue(responseContent.contains("📊 **Status:** completed"));
        assertTrue(response.isUsedMcpTools());
    }

    @Test
    void testProcessPrompt_CaseInsensitiveKeywordDetection() {
        // Test various case combinations
        String[] prompts = {
            "Show me GITHUB repositories",
            "List Repository information", 
            "Find repo details",
            "Check ISSUE status",
            "Review Pull Request",
            "Latest COMMIT info",
            "All BRANCH names"
        };
        
        Map<String, Object> mcpResult = Map.of("status", "success");
        when(chatModel.call(any())).thenReturn(mockChatResponse);
        when(gitHubMcpClient.executeGitHubQuery(anyString())).thenReturn(mcpResult);
        
        for (String prompt : prompts) {
            // Arrange
            promptRequest.setPrompt(prompt);
            
            // Act
            PromptResponse response = aiMcpService.processPrompt(promptRequest);
            
            // Assert
            assertTrue(response.isUsedMcpTools(), 
                "Should detect GitHub tools needed for prompt: " + prompt);
        }
    }

    @Test
    void testProcessPrompt_WithNullPromptRequest_ShouldThrowException() {
        // Act & Assert
        assertThrows(NullPointerException.class, 
            () -> aiMcpService.processPrompt(null));
    }

    @Test
    void testServiceInitialization_WithValidDependencies_ShouldSucceed() {
        // Act & Assert
        assertNotNull(aiMcpService);
        // Verify that the service can be instantiated with mocked dependencies
        AiMcpService newService = new AiMcpService(chatModel, gitHubMcpClient);
        assertNotNull(newService);
    }

    @Test
    void testProcessPrompt_WithSpecialCharactersInPrompt_ShouldHandleGracefully() {
        // Arrange
        promptRequest.setPrompt("Show GitHub repos with special chars: !@#$%^&*()");
        Map<String, Object> mcpResult = Map.of("status", "success");
        
        when(chatModel.call(any())).thenReturn(mockChatResponse);
        when(gitHubMcpClient.executeGitHubQuery(anyString())).thenReturn(mcpResult);
        
        // Act
        PromptResponse response = aiMcpService.processPrompt(promptRequest);
        
        // Assert
        assertTrue(response.isUsedMcpTools());
        assertNotNull(response.getResponse());
    }

    @Test
    void testProcessPrompt_WithVeryLongPrompt_ShouldHandleGracefully() {
        // Arrange
        String longPrompt = "GitHub repository ".repeat(100) + "search query";
        promptRequest.setPrompt(longPrompt);
        Map<String, Object> mcpResult = Map.of("status", "success");
        
        when(chatModel.call(any())).thenReturn(mockChatResponse);
        when(gitHubMcpClient.executeGitHubQuery(anyString())).thenReturn(mcpResult);
        
        // Act
        PromptResponse response = aiMcpService.processPrompt(promptRequest);
        
        // Assert
        assertTrue(response.isUsedMcpTools());
        assertNotNull(response.getResponse());
    }

    @Test
    void testProcessPrompt_WithEmptyPrompt_ShouldHandleGracefully() {
        // Arrange
        promptRequest.setPrompt("");
        when(chatModel.call(any())).thenReturn(mockChatResponse);
        
        // Act
        PromptResponse response = aiMcpService.processPrompt(promptRequest);
        
        // Assert
        assertNotNull(response);
        assertFalse(response.isUsedMcpTools());
        assertEquals("AI response", response.getResponse());
    }
}
