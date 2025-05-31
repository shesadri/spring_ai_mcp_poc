package com.example.springaimcp.model;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Response model for AI prompt processing
 */
public class PromptResponse {

    private String response;
    private Map<String, Object> mcpData;
    private boolean usedMcpTools;
    private LocalDateTime timestamp;
    private String status;
    private String model;

    public PromptResponse() {
        this.timestamp = LocalDateTime.now();
        this.status = "success";
    }

    public PromptResponse(String response, Map<String, Object> mcpData, boolean usedMcpTools) {
        this();
        this.response = response;
        this.mcpData = mcpData;
        this.usedMcpTools = usedMcpTools;
    }

    public PromptResponse(String response, Map<String, Object> mcpData, boolean usedMcpTools, String status) {
        this(response, mcpData, usedMcpTools);
        this.status = status;
    }

    // Getters and Setters
    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Map<String, Object> getMcpData() {
        return mcpData;
    }

    public void setMcpData(Map<String, Object> mcpData) {
        this.mcpData = mcpData;
    }

    public boolean isUsedMcpTools() {
        return usedMcpTools;
    }

    public void setUsedMcpTools(boolean usedMcpTools) {
        this.usedMcpTools = usedMcpTools;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @Override
    public String toString() {
        return "PromptResponse{" +
                "response='" + response + '\'' +
                ", mcpData=" + mcpData +
                ", usedMcpTools=" + usedMcpTools +
                ", timestamp=" + timestamp +
                ", status='" + status + '\'' +
                ", model='" + model + '\'' +
                '}';
    }
}
