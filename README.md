
```markdown
# DevOpsGPT

DevOpsGPT is a Spring Boot-based application that leverages AI to assist with DevOps, cloud computing, and software engineering tasks. It provides intelligent responses to user queries, integrates Retrieval-Augmented Generation (RAG) for context-aware answers, and supports advanced dialogue management.

## Features

- **Standard Chat**: AI-powered responses to user queries.
- **Retrieval-Augmented Generation (RAG)**: Context-aware answers using relevant documents.
- **Advanced Chat**: Stateful conversations with enhanced reasoning capabilities.
- **Document Ingestion**: Processes and stores documents for RAG-based queries.
- **Ping Endpoint**: Health check for the application.

## Technologies Used

- **Java**: Core programming language.
- **Spring Boot**: Framework for building the application.
- **Maven**: Dependency management and build tool.
- **AWS SDK**: Integration with AWS services.
- **Spring AI**: AI client for chat and vector store operations.
- **SLF4J**: Logging framework.

## Prerequisites

- **Java 21**: Ensure Java 21 is installed.
- **Maven**: Install Maven for dependency management.
- **AWS Credentials**: Configure AWS credentials for services like EC2.
- **IDE**: IntelliJ IDEA is recommended for development.

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/pearch001/DevopsGpt.git
   cd DevopsGpt
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

## Configuration

### AWS SDK
Ensure the `application.properties` file contains the correct AWS region and credentials:
```properties
aws.region=us-east-1
aws.accessKeyId=your-access-key-id
aws.secretAccessKey=your-secret-access-key
```

### Logging
SLF4J is used for logging. Logs are written to the console by default. You can configure logging in `application.properties`:
```properties
logging.level.root=INFO
logging.file.name=devopsgpt.log
```

## API Endpoints

### Health Check
- **GET** `/api/ping`
    - Response: `"Pong! DevOpsGPT is running."`

### Standard Chat
- **POST** `/api/chat`
    - Request Body:
      ```json
      {
        "sessionId": "unique-session-id",
        "message": "Your query here"
      }
      ```
    - Response:
      ```json
      {
        "reply": "AI response",
        "sessionId": "unique-session-id"
      }
      ```

### RAG Chat
- **POST** `/api/chat/rag`
    - Request Body:
      ```json
      {
        "sessionId": "unique-session-id",
        "message": "Your query here"
      }
      ```
    - Response:
      ```json
      {
        "reply": "Context-aware AI response",
        "sessionId": "unique-session-id"
      }
      ```

### Advanced Chat
- **POST** `/api/chat/advanced`
    - Request Body:
      ```json
      {
        "sessionId": "unique-session-id",
        "message": "Your query here"
      }
      ```
    - Response:
      ```json
      {
        "response": "Enhanced AI response",
        "sessionId": "unique-session-id"
      }
      ```

## Document Ingestion

Documents are ingested using the `VectorStoreIngestor` service. Ensure the documents are formatted correctly and placed in the appropriate resource directory.

## Troubleshooting

### Common Issues

#### `UnsatisfiedDependencyException`
Ensure all required beans are correctly configured and dependencies are included in `pom.xml`.

#### `ClientEndpointProvider` Error
Verify the AWS SDK version in `pom.xml` and ensure compatibility.

#### `commons-logging.jar` Conflict
Exclude `commons-logging` from dependencies in `pom.xml`:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
    <exclusions>
        <exclusion>
            <groupId>commons-logging</groupId>
            <artifactId>commons-logging</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

## Contributing

1. Fork the repository.
2. Create a new branch for your feature or bug fix.
3. Submit a pull request with a detailed description of your changes.

## License

This project is licensed under the MIT License. See the `LICENSE` file for details.

## Contact

For questions or support, contact [pearch001](https://github.com/pearch001).
```