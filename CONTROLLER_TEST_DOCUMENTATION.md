# PromptController Test Suite Documentation

## Overview
This document describes the comprehensive test suite for the `PromptController` class, which provides REST endpoints for the Spring AI MCP (Model Context Protocol) integration.

## Test Structure

### 1. Unit Tests (`PromptControllerTest.java`)
**Location**: `src/test/java/com/example/springaimcp/controller/PromptControllerTest.java`

**Purpose**: Test controller endpoints in isolation using `@WebMvcTest` with mocked dependencies.

**Framework**: Spring Boot Test with MockMvc, Mockito

**Key Test Categories**:

#### **POST /api/v1/prompt Endpoint Tests**
- `testProcessPrompt_WithValidRequest_ShouldReturnSuccessResponse()` - Tests successful prompt processing
- `testProcessPrompt_WithEmptyPrompt_ShouldReturnBadRequest()` - Tests validation for empty prompts
- `testProcessPrompt_WithNullPrompt_ShouldReturnBadRequest()` - Tests validation for null prompts
- `testProcessPrompt_WithInvalidJson_ShouldReturnBadRequest()` - Tests malformed JSON handling
- `testProcessPrompt_WithMissingRequestBody_ShouldReturnBadRequest()` - Tests missing request body
- `testProcessPrompt_WithServiceException_ShouldReturnInternalServerError()` - Tests error handling
- `testProcessPrompt_WithLongPrompt_ShouldHandleSuccessfully()` - Tests large input handling
- `testProcessPrompt_WithSpecialCharacters_ShouldHandleSuccessfully()` - Tests special character encoding
- `testProcessPrompt_WithUnicodeCharacters_ShouldHandleSuccessfully()` - Tests unicode support

#### **GET /api/v1/health Endpoint Tests**
- `testHealth_ShouldReturnHealthyStatus()` - Tests basic health check
- `testHealth_WithDifferentAcceptHeaders_ShouldReturnHealthyStatus()` - Tests various content types

#### **GET /api/v1/mcp/tools Endpoint Tests**
- `testGetMcpTools_WithAvailableTools_ShouldReturnToolsList()` - Tests successful tools retrieval
- `testGetMcpTools_WithEmptyToolsList_ShouldReturnEmptyArray()` - Tests empty tools response
- `testGetMcpTools_WithServiceException_ShouldReturnInternalServerError()` - Tests service errors
- `testGetMcpTools_WithNullResponse_ShouldReturnNull()` - Tests null response handling

#### **CORS (Cross-Origin Resource Sharing) Tests**
- `testCorsHeaders_ShouldAllowCrossOriginRequests()` - Tests CORS configuration
- `testCorsWithPromptEndpoint_ShouldAllowCrossOriginRequests()` - Tests CORS on POST endpoint

#### **Content Type Validation Tests**
- `testProcessPrompt_WithUnsupportedContentType_ShouldReturnUnsupportedMediaType()` - Tests content type validation
- `testProcessPrompt_WithXmlContentType_ShouldReturnUnsupportedMediaType()` - Tests XML rejection

#### **HTTP Method Validation Tests**
- `testPromptEndpoint_WithGetMethod_ShouldReturnMethodNotAllowed()` - Tests method restrictions
- `testPromptEndpoint_WithPutMethod_ShouldReturnMethodNotAllowed()` - Tests unsupported methods
- `testHealthEndpoint_WithPostMethod_ShouldReturnMethodNotAllowed()` - Tests GET-only endpoints
- `testMcpToolsEndpoint_WithPostMethod_ShouldReturnMethodNotAllowed()` - Tests method validation

#### **URL Path Validation Tests**
- `testInvalidEndpoint_ShouldReturnNotFound()` - Tests 404 responses
- `testEndpointWithoutApiVersion_ShouldReturnNotFound()` - Tests versioned API enforcement

#### **Response Format Tests**
- `testProcessPrompt_ResponseShouldContainAllRequiredFields()` - Tests response structure
- `testConcurrentRequests_ShouldHandleMultipleRequestsCorrectly()` - Tests concurrent request handling

### 2. Integration Tests (`PromptControllerIntegrationTest.java`)
**Location**: `src/test/java/com/example/springaimcp/controller/PromptControllerIntegrationTest.java`

**Purpose**: Test controller endpoints within a full Spring application context.

**Framework**: Spring Boot Test with full context loading

**Key Tests**:
- `contextLoads()` - Verifies Spring context loads successfully
- `testPromptController_InFullSpringContext()` - Tests prompt processing in full context
- `testHealthEndpoint_InFullSpringContext()` - Tests health endpoint with full context
- `testMcpToolsEndpoint_InFullSpringContext()` - Tests tools endpoint with full context
- `testErrorHandling_InFullSpringContext()` - Tests error handling in full context
- `testCorsConfiguration_InFullSpringContext()` - Tests CORS configuration in full context

## API Endpoints Tested

### **POST /api/v1/prompt**
- **Purpose**: Process AI prompts with GitHub MCP integration
- **Request Body**: `PromptRequest` with prompt text
- **Response**: `PromptResponse` with AI response and MCP data
- **Content Type**: `application/json`
- **Validation**: `@Valid` annotation on request body

### **GET /api/v1/health**
- **Purpose**: Health check endpoint
- **Response**: Simple text message indicating service status
- **Content Type**: `text/plain`

### **GET /api/v1/mcp/tools**
- **Purpose**: Retrieve available GitHub MCP tools
- **Response**: JSON array of tool objects
- **Content Type**: `application/json`

## Test Coverage Areas

### ✅ **Functional Testing**
- All three REST endpoints tested thoroughly
- Request/response validation
- JSON serialization/deserialization
- Service layer integration
- Error response formatting

### ✅ **Validation Testing**
- Bean validation (`@Valid`) enforcement
- Content type validation
- HTTP method restrictions
- URL path validation
- Request body validation

### ✅ **Error Handling Testing**
- Service exceptions properly caught and handled
- Appropriate HTTP status codes returned
- Error messages properly formatted
- Graceful degradation scenarios

### ✅ **Security & CORS Testing**
- CORS headers properly configured
- Cross-origin requests allowed
- Security annotations respected

### ✅ **Integration Testing**
- Full Spring context loading
- Real dependency injection
- Configuration property loading
- Bean wiring verification

## Testing Configuration

### **Dependencies Used**
```kotlin
testImplementation("org.springframework.boot:spring-boot-starter-test")
testImplementation("org.springframework.boot:spring-boot-test-autoconfigure")
testImplementation("org.mockito:mockito-core")
testImplementation("org.mockito:mockito-junit-jupiter")
testImplementation("com.fasterxml.jackson.core:jackson-databind")
```

### **Test Annotations**
- `@WebMvcTest(PromptController.class)` - For unit testing with minimal context
- `@SpringBootTest` - For integration testing with full context
- `@MockBean` - For mocking service dependencies
- `@ActiveProfiles("test")` - For test-specific configuration

### **MockMvc Configuration**
- Automatic configuration via `@WebMvcTest`
- Manual setup via `MockMvcBuilders.webAppContextSetup()` for integration tests
- JSON processing via `ObjectMapper`

## Running the Tests

### **Run All Controller Tests**
```bash
./gradlew test --tests "*PromptController*"
```

### **Run Only Unit Tests**
```bash
./gradlew test --tests "PromptControllerTest"
```

### **Run Only Integration Tests**
```bash
./gradlew test --tests "PromptControllerIntegrationTest"
```

### **Run with Coverage**
```bash
./gradlew testWithCoverage --tests "*PromptController*"
```

## Expected Test Results

### **Coverage Metrics**
- **Line Coverage**: >95% for PromptController
- **Branch Coverage**: >90% for all conditional logic
- **Method Coverage**: 100% for all public methods

### **Test Execution Time**
- **Unit Tests**: <5 seconds (lightweight MockMvc)
- **Integration Tests**: <10 seconds (full Spring context)
- **Total Execution**: <15 seconds for all controller tests

## Testing Best Practices Demonstrated

### **Unit Testing Patterns**
- **Arrange-Act-Assert**: Clear test structure
- **Mock Isolation**: Service dependencies properly mocked
- **Boundary Testing**: Edge cases and invalid inputs tested
- **Error Path Testing**: Exception scenarios covered

### **Integration Testing Patterns**
- **Context Verification**: Spring context loading validated
- **End-to-End Flow**: Complete request/response cycle tested
- **Configuration Testing**: CORS and security settings verified

### **Assertion Strategies**
- **Status Code Verification**: HTTP status codes validated
- **Content Type Verification**: Response content types checked
- **JSON Path Assertions**: Response structure validated
- **Header Verification**: CORS and custom headers checked

## Mock Strategies

### **Service Layer Mocking**
```java
@MockBean
private AiMcpService aiMcpService;

// Stubbing successful responses
when(aiMcpService.processPrompt(any(PromptRequest.class)))
    .thenReturn(mockPromptResponse);

// Stubbing exceptions
when(aiMcpService.processPrompt(any(PromptRequest.class)))
    .thenThrow(new RuntimeException("Service error"));
```

### **Response Object Construction**
```java
PromptResponse mockResponse = new PromptResponse(
    "AI response text",
    Map.of("repositories", List.of("test/repo"), "status", "success"),
    true
);
```

## Common Test Scenarios

### **Success Path Testing**
1. Valid request with proper JSON
2. Service returns successful response
3. Verify 200 OK status
4. Verify response structure and content

### **Validation Error Testing**
1. Invalid request (null, empty, malformed)
2. Verify 400 Bad Request status
3. Verify service is not called
4. Verify error response format

### **Service Error Testing**
1. Valid request processed
2. Service throws exception
3. Verify 500 Internal Server Error status
4. Verify error response with proper message

### **Method/Content Type Testing**
1. Invalid HTTP method used
2. Verify 405 Method Not Allowed status
3. Invalid content type used
4. Verify 415 Unsupported Media Type status

## Continuous Integration

### **CI/CD Integration**
- Tests run automatically on pull requests
- Coverage reports generated and enforced
- Test results published to build pipeline
- Failures block deployment

### **Quality Gates**
- Minimum 90% test coverage required
- All tests must pass before merge
- No test execution time regressions
- Performance benchmarks maintained

## Troubleshooting

### **Common Issues**
1. **Mock Setup**: Ensure all service methods are properly mocked
2. **JSON Serialization**: Verify ObjectMapper configuration
3. **Content Type**: Ensure correct MediaType constants used
4. **Assertions**: Use appropriate JsonPath expressions

### **Debug Tips**
- Enable debug logging for MockMvc: `@AutoConfigureWebMvc`
- Print request/response details: `.andDo(print())`
- Verify mock interactions: `verify(service, times(1)).method()`
- Check test configuration: `@TestPropertySource`

## Future Enhancements

### **Additional Test Scenarios**
1. **Rate Limiting Tests**: Test API rate limiting behavior
2. **Authentication Tests**: Test security configurations
3. **Performance Tests**: Load testing with MockMvc
4. **Contract Tests**: API contract verification

### **Advanced Testing Patterns**
1. **Parameterized Tests**: Test multiple input combinations
2. **Test Containers**: Integration with external services
3. **Wiremock Integration**: Mock external API dependencies
4. **Custom Matchers**: Domain-specific assertions
