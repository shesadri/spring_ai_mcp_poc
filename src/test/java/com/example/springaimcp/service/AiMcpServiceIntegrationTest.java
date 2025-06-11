package com.example.springaimcp.service;

import com.example.springaimcp.model.PromptRequest;
import com.example.springaimcp.model.PromptResponse;
import com.example.springaimcp.service.mcp.GitHubMcpClient;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Integration tests for AiMcpService
 * Tests the service in a Spring context with real dependency injection
 */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
class AiMcpServiceIntegrationTest {

    @Autowired
    private AiMcpService aiMcpService;

    @MockBean
    private ChatModel chatModel;

    @MockBean
    private GitHubMcpClient gitHubMcpClient;

    @Test
    void contextLoads() {
        assertNotNull(aiMcpService);
    }

    @Test
    void testServiceIntegration_WithMockedDependencies() {
        // Arrange
        PromptRequest request = new PromptRequest();
        request.setPrompt("Test GitHub integration");
        
        Map<String, Object> mcpResult = Map.of(
            "repositories", List.of("test/repo"),
            "status", "success"
        );
        
        // Mock the dependencies
        when(gitHubMcpClient.executeGitHubQuery(anyString())).thenReturn(mcpResult);
        when(gitHubMcpClient.getAvailableTools()).thenReturn(
            List.of(Map.of("name", "test_tool", "description", "Test tool"))
        );

        // Act
        List<Map<String, Object>> tools = aiMcpService.getAvailableTools();

        // Assert
        assertNotNull(tools);
        assertFalse(tools.isEmpty());
        assertEquals("test_tool", tools.get(0).get("name"));
    }

    @Test
    void testServiceConfiguration_VerifyBeanWiring() {
        // Verify that all required beans are properly wired
        assertNotNull(aiMcpService);
        
        // Test that the service can be called without exceptions due to configuration issues
        List<Map<String, Object>> tools = aiMcpService.getAvailableTools();
        
        // Since we mocked the client, this should return the mocked response or empty list
        assertNotNull(tools);
    }

    @Test
    void testServiceBehavior_InSpringContext() {
        // Arrange
        PromptRequest request = new PromptRequest();
        request.setPrompt("Simple non-GitHub prompt");
        
        // Act & Assert - Should not throw any configuration-related exceptions
        assertDoesNotThrow(() -> {
            // The actual call might fail due to missing real ChatModel, but configuration should be fine
            try {
                aiMcpService.processPrompt(request);
            } catch (RuntimeException e) {
                // Expected due to mocked dependencies, but configuration errors would be different
                assertTrue(e.getMessage().contains("Failed to process prompt") || 
                          e.getCause() instanceof NullPointerException);
            }
        });
    }
}
