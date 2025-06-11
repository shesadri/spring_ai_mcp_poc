# AiMcpService Test Suite Documentation

## Overview
This document describes the comprehensive test suite for the `AiMcpService` class, which integrates Spring AI with GitHub MCP (Model Context Protocol) server.

## Test Structure

### 1. Unit Tests (`AiMcpServiceTest.java`)
Located at: `src/test/java/com/example/springaimcp/service/AiMcpServiceTest.java`

**Purpose**: Test individual methods in isolation using mocked dependencies.

**Key Test Categories**:

#### Basic Functionality Tests
- `testProcessPrompt_WithNonGitHubPrompt_ShouldReturnBasicResponse()` - Tests normal AI responses without MCP integration
- `testGetAvailableTools_ShouldReturnToolsFromMcpClient()` - Verifies tool retrieval from MCP client

#### GitHub Keyword Detection Tests
- `testProcessPrompt_WithGitHubKeyword_ShouldUseMcpTools()` - Tests "github" keyword detection
- `testProcessPrompt_WithRepositoryKeyword_ShouldUseMcpTools()` - Tests "repository" keyword detection
- `testProcessPrompt_WithIssueKeyword_ShouldUseMcpTools()` - Tests "issue" keyword detection
- `testProcessPrompt_WithPullRequestKeyword_ShouldUseMcpTools()` - Tests "pull request" keyword detection
- `testProcessPrompt_WithCommitKeyword_ShouldUseMcpTools()` - Tests "commit" keyword detection
- `testProcessPrompt_WithBranchKeyword_ShouldUseMcpTools()` - Tests "branch" keyword detection

#### Edge Case and Error Handling Tests
- `testProcessPrompt_WithEmptyMcpResult_ShouldHandleGracefully()` - Tests empty MCP responses
- `testProcessPrompt_WithNullMcpResult_ShouldHandleGracefully()` - Tests null MCP responses
- `testProcessPrompt_WithChatModelException_ShouldThrowRuntimeException()` - Tests ChatModel failures
- `testProcessPrompt_WithMcpClientException_ShouldThrowRuntimeException()` - Tests MCP client failures
- `testProcessPrompt_WithNullPromptRequest_ShouldThrowException()` - Tests null input validation

#### Advanced Functionality Tests
- `testProcessPrompt_WithComplexMcpData_ShouldEnhanceResponseProperly()` - Tests response enhancement with rich MCP data
- `testProcessPrompt_CaseInsensitiveKeywordDetection()` - Tests case-insensitive keyword matching
- `testProcessPrompt_WithSpecialCharactersInPrompt_ShouldHandleGracefully()` - Tests special character handling
- `testProcessPrompt_WithVeryLongPrompt_ShouldHandleGracefully()` - Tests large input handling
- `testProcessPrompt_WithEmptyPrompt_ShouldHandleGracefully()` - Tests empty prompt handling

#### Service Initialization Tests
- `testServiceInitialization_WithValidDependencies_ShouldSucceed()` - Tests constructor and dependency injection

### 2. Integration Tests (`AiMcpServiceIntegrationTest.java`)
Located at: `src/test/java/com/example/springaimcp/service/AiMcpServiceIntegrationTest.java`

**Purpose**: Test the service within a Spring application context to verify proper configuration and bean wiring.

**Key Tests**:
- `contextLoads()` - Verifies Spring context loads successfully
- `testServiceIntegration_WithMockedDependencies()` - Tests service with mocked dependencies in Spring context
- `testServiceConfiguration_VerifyBeanWiring()` - Verifies proper dependency injection
- `testServiceBehavior_InSpringContext()` - Tests service behavior within Spring framework

## Test Configuration

### Test Dependencies Added
The following testing dependencies have been added to `build.gradle.kts`:

```kotlin
testImplementation("org.springframework.boot:spring-boot-starter-test")
testImplementation("io.projectreactor:reactor-test")
testImplementation("org.mockito:mockito-core")
testImplementation("org.mockito:mockito-junit-jupiter")
testImplementation("org.junit.jupiter:junit-jupiter-api")
testImplementation("org.junit.jupiter:junit-jupiter-engine")
testImplementation("org.assertj:assertj-core")
testImplementation("org.springframework.boot:spring-boot-test-autoconfigure")
```

### Jacoco Code Coverage
- Added Jacoco plugin for code coverage analysis
- Minimum coverage threshold set to 80%
- Generates XML, HTML, and CSV reports

### Test Configuration Properties
Test-specific configuration in `src/test/resources/application-test.properties`:
- Mock OpenAI API configuration
- Debug logging for troubleshooting
- In-memory database setup
- Disabled web environment for unit tests

## Running the Tests

### Run All Tests
```bash
./gradlew test
```

### Run Tests with Coverage Report
```bash
./gradlew testWithCoverage
```

### Run Only Unit Tests
```bash
./gradlew test --tests "*AiMcpServiceTest"
```

### Run Only Integration Tests
```bash
./gradlew test --tests "*AiMcpServiceIntegrationTest"
```

### Generate Coverage Report Only
```bash
./gradlew jacocoTestReport
```

### View Coverage Report
After running tests with coverage, open: `build/reports/jacoco/test/html/index.html`

## Test Coverage Areas

The test suite provides comprehensive coverage for:

### ✅ Covered Functionality
- All public methods of `AiMcpService`
- Keyword detection logic (case-insensitive)
- MCP tool integration flow
- Error handling and exception scenarios
- Response enhancement with MCP data
- Edge cases (null, empty, malformed inputs)
- Spring context integration
- Dependency injection verification

### 🎯 Coverage Metrics Target
- **Line Coverage**: > 80%
- **Branch Coverage**: > 75%
- **Method Coverage**: 100%

## Mocking Strategy

### Dependencies Mocked
1. **ChatModel**: Spring AI's chat model interface
2. **GitHubMcpClient**: Custom MCP client for GitHub integration

### Mock Behaviors
- Configurable AI responses
- Controlled MCP tool execution results
- Exception simulation for error testing
- Various response scenarios (empty, null, complex data)

## Best Practices Demonstrated

### Test Structure
- Clear test method naming convention
- Arrange-Act-Assert pattern
- Proper setup and teardown
- Comprehensive assertions

### Error Testing
- Exception type verification
- Error message validation
- Cause chain verification
- Graceful degradation testing

### Performance Considerations
- Efficient mock setup
- Minimal test execution time
- Resource cleanup
- Parallel test execution support

## Continuous Integration

These tests are designed to run in CI/CD pipelines with:
- No external dependencies required
- Fast execution time
- Clear failure reporting
- Coverage threshold enforcement

## Future Enhancements

Potential areas for additional testing:
1. Performance benchmarking tests
2. Concurrent request handling tests
3. Memory usage optimization tests
4. End-to-end tests with real MCP server
5. Parameterized tests for various input combinations

## Troubleshooting

### Common Issues
1. **Mock Configuration**: Ensure all required mock behaviors are defined
2. **Spring Context**: Verify test configuration properties are correct
3. **Dependencies**: Check that all test dependencies are properly imported
4. **Coverage**: Review Jacoco reports for uncovered code paths

### Debug Tips
- Enable debug logging in test configuration
- Use `@MockitoSettings(strictness = Strictness.LENIENT)` for flexible mocking
- Add breakpoints in test methods for step-through debugging
- Check test execution order for any dependencies between tests
