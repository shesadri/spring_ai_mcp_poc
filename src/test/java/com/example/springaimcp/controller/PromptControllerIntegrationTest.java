package com.example.springaimcp.controller;

import com.example.springaimcp.model.PromptRequest;
import com.example.springaimcp.model.PromptResponse;
import com.example.springaimcp.service.AiMcpService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for PromptController
 * Tests the controller within a full Spring application context
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
class PromptControllerIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private AiMcpService aiMcpService;

    @Autowired
    private ObjectMapper objectMapper;

    private PromptRequest validPromptRequest;
    private PromptResponse mockPromptResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        validPromptRequest = new PromptRequest();
        validPromptRequest.setPrompt("Integration test prompt for GitHub");

        mockPromptResponse = new PromptResponse(
            "Integration test response",
            Map.of("repositories", List.of("integration/test"), "status", "success"),
            true
        );
    }

    @Test
    void contextLoads() {
        // Verify that the Spring context loads successfully
        assert webApplicationContext != null;
    }

    @Test
    void testPromptController_InFullSpringContext() throws Exception {
        // Arrange
        when(aiMcpService.processPrompt(any(PromptRequest.class))).thenReturn(mockPromptResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/prompt")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validPromptRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.response").value("Integration test response"))
                .andExpect(jsonPath("$.usedMcpTools").value(true))
                .andExpect(jsonPath("$.mcpData.status").value("success"));
    }

    @Test
    void testHealthEndpoint_InFullSpringContext() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Spring AI MCP PoC is running!"));
    }

    @Test
    void testMcpToolsEndpoint_InFullSpringContext() throws Exception {
        // Arrange
        List<Map<String, Object>> mockTools = List.of(
            Map.of("name", "integration_tool", "description", "Integration test tool")
        );
        when(aiMcpService.getAvailableTools()).thenReturn(mockTools);

        // Act & Assert
        mockMvc.perform(get("/api/v1/mcp/tools"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("integration_tool"));
    }

    @Test
    void testErrorHandling_InFullSpringContext() throws Exception {
        // Arrange
        when(aiMcpService.processPrompt(any(PromptRequest.class)))
            .thenThrow(new RuntimeException("Integration test error"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/prompt")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validPromptRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.response").value("Error processing prompt: Integration test error"));
    }

    @Test
    void testCorsConfiguration_InFullSpringContext() throws Exception {
        // Arrange
        when(aiMcpService.processPrompt(any(PromptRequest.class))).thenReturn(mockPromptResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/prompt")
                .header("Origin", "http://localhost:3000")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validPromptRequest)))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "*"));
    }
}
