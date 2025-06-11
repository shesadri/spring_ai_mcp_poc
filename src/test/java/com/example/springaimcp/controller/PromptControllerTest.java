package com.example.springaimcp.controller;

import com.example.springaimcp.model.PromptRequest;
import com.example.springaimcp.model.PromptResponse;
import com.example.springaimcp.service.AiMcpService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Comprehensive test suite for PromptController
 * Tests all REST endpoints with various scenarios including success, validation, and error cases
 */
@WebMvcTest(PromptController.class)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class PromptControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AiMcpService aiMcpService;

    @Autowired
    private ObjectMapper objectMapper;

    private PromptRequest validPromptRequest;
    private PromptResponse mockPromptResponse;

    @BeforeEach
    void setUp() {
        validPromptRequest = new PromptRequest();
        validPromptRequest.setPrompt("Test prompt for GitHub integration");

        mockPromptResponse = new PromptResponse(
            "AI response with GitHub integration",
            Map.of("repositories", List.of("test/repo"), "status", "success"),
            true
        );
    }

    // ========== POST /api/v1/prompt Tests ==========

    @Test
    void testProcessPrompt_WithValidRequest_ShouldReturnSuccessResponse() throws Exception {
        // Arrange
        when(aiMcpService.processPrompt(any(PromptRequest.class))).thenReturn(mockPromptResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/prompt")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validPromptRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.response").value("AI response with GitHub integration"))
                .andExpect(jsonPath("$.usedMcpTools").value(true))
                .andExpect(jsonPath("$.mcpData.status").value("success"))
                .andExpect(jsonPath("$.mcpData.repositories[0]").value("test/repo"));

        // Verify service interaction
        verify(aiMcpService, times(1)).processPrompt(any(PromptRequest.class));
    }

    @Test
    void testProcessPrompt_WithEmptyPrompt_ShouldReturnBadRequest() throws Exception {
        // Arrange
        PromptRequest emptyRequest = new PromptRequest();
        emptyRequest.setPrompt("");

        // Act & Assert
        mockMvc.perform(post("/api/v1/prompt")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyRequest)))
                .andExpect(status().isBadRequest());

        // Verify service was not called
        verify(aiMcpService, never()).processPrompt(any());
    }

    @Test
    void testProcessPrompt_WithNullPrompt_ShouldReturnBadRequest() throws Exception {
        // Arrange
        PromptRequest nullRequest = new PromptRequest();
        nullRequest.setPrompt(null);

        // Act & Assert
        mockMvc.perform(post("/api/v1/prompt")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nullRequest)))
                .andExpect(status().isBadRequest());

        // Verify service was not called
        verify(aiMcpService, never()).processPrompt(any());
    }

    @Test
    void testProcessPrompt_WithInvalidJson_ShouldReturnBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/prompt")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ invalid json }"))
                .andExpect(status().isBadRequest());

        // Verify service was not called
        verify(aiMcpService, never()).processPrompt(any());
    }

    @Test
    void testProcessPrompt_WithMissingRequestBody_ShouldReturnBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/prompt")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        // Verify service was not called
        verify(aiMcpService, never()).processPrompt(any());
    }

    @Test
    void testProcessPrompt_WithServiceException_ShouldReturnInternalServerError() throws Exception {
        // Arrange
        when(aiMcpService.processPrompt(any(PromptRequest.class)))
            .thenThrow(new RuntimeException("Service error"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/prompt")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validPromptRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.response").value("Error processing prompt: Service error"))
                .andExpect(jsonPath("$.usedMcpTools").value(false))
                .andExpect(jsonPath("$.mcpData").doesNotExist());

        // Verify service interaction
        verify(aiMcpService, times(1)).processPrompt(any(PromptRequest.class));
    }

    @Test
    void testProcessPrompt_WithLongPrompt_ShouldHandleSuccessfully() throws Exception {
        // Arrange
        String longPrompt = "This is a very long prompt ".repeat(100) + "with GitHub integration request";
        PromptRequest longRequest = new PromptRequest();
        longRequest.setPrompt(longPrompt);

        PromptResponse longResponse = new PromptResponse(
            "AI response for long prompt",
            Map.of("status", "processed"),
            false
        );

        when(aiMcpService.processPrompt(any(PromptRequest.class))).thenReturn(longResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/prompt")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(longRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("AI response for long prompt"))
                .andExpect(jsonPath("$.usedMcpTools").value(false));

        verify(aiMcpService, times(1)).processPrompt(any(PromptRequest.class));
    }

    @Test
    void testProcessPrompt_WithSpecialCharacters_ShouldHandleSuccessfully() throws Exception {
        // Arrange
        PromptRequest specialCharRequest = new PromptRequest();
        specialCharRequest.setPrompt("GitHub query with special chars: !@#$%^&*()_+{}|:<>?");

        when(aiMcpService.processPrompt(any(PromptRequest.class))).thenReturn(mockPromptResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/prompt")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(specialCharRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").exists());

        verify(aiMcpService, times(1)).processPrompt(any(PromptRequest.class));
    }

    @Test
    void testProcessPrompt_WithUnicodeCharacters_ShouldHandleSuccessfully() throws Exception {
        // Arrange
        PromptRequest unicodeRequest = new PromptRequest();
        unicodeRequest.setPrompt("GitHub query with unicode characters: 日本語, العربية, 中文");

        when(aiMcpService.processPrompt(any(PromptRequest.class))).thenReturn(mockPromptResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/prompt")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(unicodeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").exists());

        verify(aiMcpService, times(1)).processPrompt(any(PromptRequest.class));
    }

    // ========== GET /api/v1/health Tests ==========

    @Test
    void testHealth_ShouldReturnHealthyStatus() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"))
                .andExpect(content().string("Spring AI MCP PoC is running!"));

        // Verify no service interaction
        verifyNoInteractions(aiMcpService);
    }

    @Test
    void testHealth_WithDifferentAcceptHeaders_ShouldReturnHealthyStatus() throws Exception {
        // Test with JSON accept header
        mockMvc.perform(get("/api/v1/health")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Spring AI MCP PoC is running!"));

        // Test with XML accept header
        mockMvc.perform(get("/api/v1/health")
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andExpect(content().string("Spring AI MCP PoC is running!"));
    }

    // ========== GET /api/v1/mcp/tools Tests ==========

    @Test
    void testGetMcpTools_WithAvailableTools_ShouldReturnToolsList() throws Exception {
        // Arrange
        List<Map<String, Object>> mockTools = List.of(
            Map.of("name", "search_repositories", "description", "Search GitHub repositories"),
            Map.of("name", "get_issues", "description", "Get repository issues"),
            Map.of("name", "create_pull_request", "description", "Create a new pull request")
        );

        when(aiMcpService.getAvailableTools()).thenReturn(mockTools);

        // Act & Assert
        mockMvc.perform(get("/api/v1/mcp/tools"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].name").value("search_repositories"))
                .andExpect(jsonPath("$[0].description").value("Search GitHub repositories"))
                .andExpect(jsonPath("$[1].name").value("get_issues"))
                .andExpect(jsonPath("$[2].name").value("create_pull_request"));

        // Verify service interaction
        verify(aiMcpService, times(1)).getAvailableTools();
    }

    @Test
    void testGetMcpTools_WithEmptyToolsList_ShouldReturnEmptyArray() throws Exception {
        // Arrange
        when(aiMcpService.getAvailableTools()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/v1/mcp/tools"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(aiMcpService, times(1)).getAvailableTools();
    }

    @Test
    void testGetMcpTools_WithServiceException_ShouldReturnInternalServerError() throws Exception {
        // Arrange
        when(aiMcpService.getAvailableTools()).thenThrow(new RuntimeException("MCP service unavailable"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/mcp/tools"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"))
                .andExpect(content().string("Error fetching MCP tools: MCP service unavailable"));

        verify(aiMcpService, times(1)).getAvailableTools();
    }

    @Test
    void testGetMcpTools_WithNullResponse_ShouldReturnNull() throws Exception {
        // Arrange
        when(aiMcpService.getAvailableTools()).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/v1/mcp/tools"))
                .andExpect(status().isOk())
                .andExpect(content().string("null"));

        verify(aiMcpService, times(1)).getAvailableTools();
    }

    // ========== CORS Tests ==========

    @Test
    void testCorsHeaders_ShouldAllowCrossOriginRequests() throws Exception {
        // Act & Assert
        mockMvc.perform(options("/api/v1/health")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "*"));
    }

    @Test
    void testCorsWithPromptEndpoint_ShouldAllowCrossOriginRequests() throws Exception {
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

    // ========== Content Type Tests ==========

    @Test
    void testProcessPrompt_WithUnsupportedContentType_ShouldReturnUnsupportedMediaType() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/prompt")
                .contentType(MediaType.TEXT_PLAIN)
                .content("plain text prompt"))
                .andExpect(status().isUnsupportedMediaType());

        verify(aiMcpService, never()).processPrompt(any());
    }

    @Test
    void testProcessPrompt_WithXmlContentType_ShouldReturnUnsupportedMediaType() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/prompt")
                .contentType(MediaType.APPLICATION_XML)
                .content("<prompt>test</prompt>"))
                .andExpect(status().isUnsupportedMediaType());

        verify(aiMcpService, never()).processPrompt(any());
    }

    // ========== HTTP Method Tests ==========

    @Test
    void testPromptEndpoint_WithGetMethod_ShouldReturnMethodNotAllowed() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/prompt"))
                .andExpect(status().isMethodNotAllowed());

        verify(aiMcpService, never()).processPrompt(any());
    }

    @Test
    void testPromptEndpoint_WithPutMethod_ShouldReturnMethodNotAllowed() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/api/v1/prompt")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validPromptRequest)))
                .andExpect(status().isMethodNotAllowed());

        verify(aiMcpService, never()).processPrompt(any());
    }

    @Test
    void testHealthEndpoint_WithPostMethod_ShouldReturnMethodNotAllowed() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/health"))
                .andExpect(status().isMethodNotAllowed());

        verify(aiMcpService, never()).getAvailableTools();
    }

    @Test
    void testMcpToolsEndpoint_WithPostMethod_ShouldReturnMethodNotAllowed() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/mcp/tools"))
                .andExpect(status().isMethodNotAllowed());

        verify(aiMcpService, never()).getAvailableTools();
    }

    // ========== URL Path Tests ==========

    @Test
    void testInvalidEndpoint_ShouldReturnNotFound() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/invalid"))
                .andExpect(status().isNotFound());

        verifyNoInteractions(aiMcpService);
    }

    @Test
    void testEndpointWithoutApiVersion_ShouldReturnNotFound() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/prompt"))
                .andExpect(status().isNotFound());

        verifyNoInteractions(aiMcpService);
    }

    // ========== Response Format Tests ==========

    @Test
    void testProcessPrompt_ResponseShouldContainAllRequiredFields() throws Exception {
        // Arrange
        when(aiMcpService.processPrompt(any(PromptRequest.class))).thenReturn(mockPromptResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/prompt")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validPromptRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").exists())
                .andExpect(jsonPath("$.usedMcpTools").exists())
                .andExpect(jsonPath("$.mcpData").exists())
                .andExpect(jsonPath("$.response").isString())
                .andExpect(jsonPath("$.usedMcpTools").isBoolean());
    }

    // ========== Concurrent Request Tests ==========

    @Test
    void testConcurrentRequests_ShouldHandleMultipleRequestsCorrectly() throws Exception {
        // Arrange
        when(aiMcpService.processPrompt(any(PromptRequest.class))).thenReturn(mockPromptResponse);

        // Act & Assert - Simulate concurrent requests
        ResultActions[] results = new ResultActions[5];
        for (int i = 0; i < 5; i++) {
            results[i] = mockMvc.perform(post("/api/v1/prompt")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validPromptRequest)));
        }

        // Verify all requests succeeded
        for (ResultActions result : results) {
            result.andExpect(status().isOk())
                   .andExpect(jsonPath("$.response").exists());
        }

        // Verify service was called for each request
        verify(aiMcpService, times(5)).processPrompt(any(PromptRequest.class));
    }
}
