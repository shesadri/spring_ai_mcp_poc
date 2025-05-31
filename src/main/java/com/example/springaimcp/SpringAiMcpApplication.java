package com.example.springaimcp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot Application main class for Spring AI MCP Integration PoC
 * 
 * This application demonstrates integration between:
 * - Spring AI framework
 * - GitHub MCP (Model Context Protocol) server
 * - OpenAI LLM for natural language processing
 */
@SpringBootApplication
public class SpringAiMcpApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAiMcpApplication.class, args);
    }

}