# Spring AI MCP PoC

A Proof of Concept demonstrating integration between Spring AI framework, GitHub MCP (Model Context Protocol) server, and LLM models.

## Overview

This project showcases how to build an AI-powered application that can:
- Process natural language prompts using Spring AI
- Integrate with GitHub through MCP (Model Context Protocol)
- Leverage OpenAI's GPT models for intelligent responses
- Provide GitHub-specific functionality through function calling

## Architecture

```
User Request → Spring Boot API → Spring AI → OpenAI LLM
                     ↓
              GitHub MCP Server → GitHub API
```

## Features

- **Spring AI Integration**: Leverage Spring AI framework for LLM interactions
- **GitHub MCP Support**: Connect to GitHub through Model Context Protocol
- **Function Calling**: AI can invoke GitHub operations based on user prompts
- **RESTful API**: Clean REST endpoints for prompt processing
- **Configurable**: Easy configuration for different environments
- **Error Handling**: Robust error handling and logging

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- OpenAI API key
- GitHub MCP server running (optional for basic functionality)

## Setup

### 1. Clone the Repository

```bash
git clone https://github.com/shesadri/spring_ai_mcp_poc.git
cd spring_ai_mcp_poc
```

### 2. Configure Environment Variables

Create a `.env` file or set environment variables:

```bash
export OPENAI_API_KEY="your-openai-api-key"
export GITHUB_MCP_SERVER_URL="http://localhost:3000"  # Optional
export GITHUB_TOKEN="your-github-token"  # Optional for enhanced features
```

### 3. Configure Application Properties

Edit `src/main/resources/application.yml` or use environment variables:

```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY:your-openai-api-key-here}
      
app:
  github:
    mcp:
      server:
        url: ${GITHUB_MCP_SERVER_URL:http://localhost:3000}
```

### 4. Build and Run

```bash
# Build the project
mvn clean compile

# Run the application
mvn spring-boot:run

# Or run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

The application will start on `http://localhost:8080`

## Usage

### Health Check

```bash
curl http://localhost:8080/api/v1/health
```

### Process a Prompt

```bash
curl -X POST http://localhost:8080/api/v1/prompt \
  -H "Content-Type: application/json" \
  -d '{
    "prompt": "Tell me about Spring AI framework",
    "enableMcpTools": true
  }'
```

### GitHub-specific Prompts

```bash
curl -X POST http://localhost:8080/api/v1/prompt \
  -H "Content-Type: application/json" \
  -d '{
    "prompt": "Show me information about my GitHub repositories",
    "enableMcpTools": true
  }'
```

### Get Available MCP Tools

```bash
curl http://localhost:8080/api/v1/mcp/tools
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/health` | Health check |
| POST | `/api/v1/prompt` | Process AI prompt |
| GET | `/api/v1/mcp/tools` | Get available MCP tools |

## Request/Response Format

### Prompt Request

```json
{
  "prompt": "Your question or instruction",
  "context": "Optional context",
  "enableMcpTools": true,
  "githubToken": "optional-github-token"
}
```

### Prompt Response

```json
{
  "response": "AI generated response",
  "mcpData": {
    "repositories": [...],
    "status": "success"
  },
  "usedMcpTools": true,
  "timestamp": "2024-01-01T12:00:00",
  "status": "success",
  "model": "gpt-4"
}
```

## Configuration

### Environment Variables

- `OPENAI_API_KEY`: Your OpenAI API key (required)
- `GITHUB_MCP_SERVER_URL`: URL of your GitHub MCP server (optional)
- `GITHUB_TOKEN`: GitHub personal access token (optional)
- `PORT`: Server port (default: 8080)

### Application Properties

See `application.yml` for detailed configuration options including:
- AI model settings
- Server configuration
- CORS settings
- Logging levels
- Management endpoints

## GitHub MCP Server

This application expects a GitHub MCP server to be running for enhanced GitHub functionality. The MCP server should provide:

- Repository information endpoints
- Issue management capabilities
- Pull request operations
- Code search functionality

If no MCP server is available, the application will still work but with limited GitHub integration.

## Development

### Running Tests

```bash
mvn test
```

### Development Profile

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Building for Production

```bash
mvn clean package
java -jar target/spring-ai-mcp-poc-1.0.0-SNAPSHOT.jar
```

## Project Structure

```
src/
├── main/
│   ├── java/com/example/springaimcp/
│   │   ├── SpringAiMcpApplication.java     # Main application class
│   │   ├── config/
│   │   │   └── SpringAiConfig.java         # Spring AI configuration
│   │   ├── controller/
│   │   │   └── PromptController.java       # REST API endpoints
│   │   ├── service/
│   │   │   ├── AiMcpService.java          # Main service orchestrating AI and MCP
│   │   │   └── mcp/
│   │   │       └── GitHubMcpClient.java   # GitHub MCP client
│   │   └── model/
│   │       ├── PromptRequest.java         # Request model
│   │       └── PromptResponse.java        # Response model
│   └── resources/
│       ├── application.yml                # Main configuration
│       └── application.properties         # Alternative configuration
└── test/
    └── java/
        └── ... # Test classes
```

## Technologies Used

- **Spring Boot 3.2.5**: Application framework
- **Spring AI 1.0.0-M1**: AI integration framework
- **OpenAI GPT-4**: Large Language Model
- **WebFlux**: Reactive web client for MCP communication
- **Jackson**: JSON processing
- **Maven**: Build tool

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## License

This project is licensed under the MIT License.

## Troubleshooting

### Common Issues

1. **OpenAI API Key Issues**
   - Ensure your API key is valid and has sufficient credits
   - Check the environment variable is properly set

2. **MCP Server Connection Issues**
   - Verify the MCP server is running
   - Check the server URL configuration
   - Review network connectivity

3. **GitHub Integration Issues**
   - Ensure GitHub token has proper permissions
   - Verify MCP server GitHub configuration

### Logs

Check application logs for detailed error information:

```bash
# View logs in development
tail -f logs/spring-ai-mcp-poc.log

# Increase log level for debugging
export LOGGING_LEVEL_COM_EXAMPLE_SPRINGAIMCP=DEBUG
```

## Support

For issues and questions:
- Create an issue in this repository
- Check the Spring AI documentation
- Review OpenAI API documentation
