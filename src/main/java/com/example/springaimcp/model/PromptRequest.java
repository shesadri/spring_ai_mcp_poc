package com.example.springaimcp.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request model for AI prompt processing
 */
public class PromptRequest {

    @NotBlank(message = "Prompt cannot be empty")
    @Size(max = 2000, message = "Prompt cannot exceed 2000 characters")
    private String prompt;

    private String context;
    private boolean enableMcpTools = true;
    private String githubToken;

    public PromptRequest() {}

    public PromptRequest(String prompt) {
        this.prompt = prompt;
    }

    public PromptRequest(String prompt, String context, boolean enableMcpTools) {
        this.prompt = prompt;
        this.context = context;
        this.enableMcpTools = enableMcpTools;
    }

    // Getters and Setters
    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public boolean isEnableMcpTools() {
        return enableMcpTools;
    }

    public void setEnableMcpTools(boolean enableMcpTools) {
        this.enableMcpTools = enableMcpTools;
    }

    public String getGithubToken() {
        return githubToken;
    }

    public void setGithubToken(String githubToken) {
        this.githubToken = githubToken;
    }

    @Override
    public String toString() {
        return "PromptRequest{" +
                "prompt='" + prompt + '\'' +
                ", context='" + context + '\'' +
                ", enableMcpTools=" + enableMcpTools +
                ", githubToken='" + (githubToken != null ? "[REDACTED]" : "null") + '\'' +
                '}';
    }
}
