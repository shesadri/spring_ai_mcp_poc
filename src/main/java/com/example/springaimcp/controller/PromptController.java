package com.example.springaimcp.controller;

import com.example.springaimcp.model.PromptRequest;
import com.example.springaimcp.model.PromptResponse;
import com.example.springaimcp.service.AiMcpService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for handling AI prompts with GitHub MCP integration
 */
@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class PromptController {

    private static final Logger logger = LoggerFactory.getLogger(PromptController.class);

    private final AiMcpService aiMcpService;

    @Autowired
    public PromptController(AiMcpService aiMcpService) {
        this.aiMcpService = aiMcpService;
    }

    /**
     * Process a prompt using Spring AI with GitHub MCP server integration
     * 
     * @param request The prompt request containing user input
     * @return PromptResponse with AI-generated response and MCP tool usage
     */
    @PostMapping("/prompt")
    public ResponseEntity<PromptResponse> processPrompt(@Valid @RequestBody PromptRequest request) {
        logger.info("Processing prompt: {}", request.getPrompt());
        
        try {
            PromptResponse response = aiMcpService.processPrompt(request);
            logger.info("Prompt processed successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error processing prompt: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(new PromptResponse("Error processing prompt: " + e.getMessage(), null, false));
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Spring AI MCP PoC is running!");
    }

    /**
     * Get available GitHub MCP tools
     */
    @GetMapping("/mcp/tools")
    public ResponseEntity<?> getMcpTools() {
        try {
            var tools = aiMcpService.getAvailableTools();
            return ResponseEntity.ok(tools);
        } catch (Exception e) {
            logger.error("Error fetching MCP tools: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body("Error fetching MCP tools: " + e.getMessage());
        }
    }
}
