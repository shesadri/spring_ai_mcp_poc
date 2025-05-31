plugins {
    id("org.springframework.boot") version "3.2.5"
    id("io.spring.dependency-management") version "1.1.4"
    java
}

group = "com.example"
version = "1.0.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
}

extra["springAiVersion"] = "1.0.0-M1"

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-logging")
    
    // Spring AI Dependencies
    implementation("org.springframework.ai:spring-ai-openai-spring-boot-starter:${property("springAiVersion")}")
    
    // JSON Processing
    implementation("com.fasterxml.jackson.core:jackson-databind")
    
    // Development Tools
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    
    // Annotation Processors
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    
    // Test Dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.ai:spring-ai-bom:${property("springAiVersion")}")
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

// Custom tasks
tasks.register<JavaExec>("runDev") {
    description = "Run the application with dev profile"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("com.example.springaimcp.SpringAiMcpApplication")
    args = listOf("--spring.profiles.active=dev")
}

tasks.register<JavaExec>("runProd") {
    description = "Run the application with prod profile"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("com.example.springaimcp.SpringAiMcpApplication")
    args = listOf("--spring.profiles.active=prod")
}

// Bootjar configuration
tasks.named<Jar>("jar") {
    enabled = false
    archiveClassifier.set("")
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = true
    archiveClassifier.set("")
    mainClass.set("com.example.springaimcp.SpringAiMcpApplication")
}
