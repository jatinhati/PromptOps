package dev.pearch001.devopsgpt.service;

// src/main/java/com/devopsgpt/service/CommandService.java

import dev.pearch001.devopsgpt.model.CommandResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class CommandService {

    private static final Logger logger = LoggerFactory.getLogger(CommandService.class);
    private final ChatClient chatClient;
    private final Path scriptsDir = Paths.get("scripts");

    public CommandService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
        try {
            // Create the 'scripts' directory if it doesn't exist
            if (!Files.exists(scriptsDir)) {
                Files.createDirectories(scriptsDir);
                logger.info("Created scripts directory at: {}", scriptsDir.toAbsolutePath());
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create scripts directory", e);
        }
    }

    /**
     * Generates a command from a plain-text task using an LLM with a JSON output parser.
     *
     * @param task The user's task description.
     * @return A CommandResponse object with the generated command and explanation.
     */
    public CommandResponse generateCommand(String task) {
        logger.info("Generating command for task: '{}'", task);

        // Use a BeanOutputParser to instruct the LLM to return a JSON object
        // that maps directly to our CommandResponse class.
        var outputParser = new BeanOutputConverter<>(CommandResponse.class);

        String systemPrompt = """
            You are an expert DevOps engineer. Your task is to generate a single, executable shell command
            (e.g., for bash, kubectl, docker, terraform) based on the user's request.
            You MUST return the output in the specified JSON format.
            Do not include any other text, explanations, or markdown formatting outside of the JSON structure.
            {format}
            """;

        String response = chatClient.prompt()
                .system(p -> p.text(systemPrompt).param("format", outputParser.getFormat()))
                .user(task)
                .call().content();



        // The parser automatically converts the LLM's JSON output into our Java bean.
        if (response == null) {
            logger.warn("Response from chat client is null.");
            return null; // Or throw an exception, depending on your application's requirements
        }

        return outputParser.convert(response);
    }

    /**
     * Simulates the execution of a given command.
     *
     * @param command The command to simulate.
     * @return A string containing fake log output.
     */
    public String simulateExecution(String command) {
        logger.info("Simulating execution of command: '{}'", command);

        // Save the generated script to a file
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = "script-" + timestamp + ".sh";
        try {
            Files.writeString(scriptsDir.resolve(filename), command);
            logger.info("Command saved to {}", scriptsDir.resolve(filename));
        } catch (IOException e) {
            logger.error("Failed to save script", e);
        }

        // Generate fake log output for the simulation
        StringBuilder fakeLogs = new StringBuilder();
        fakeLogs.append("SIMULATION START: Executing '").append(command).append("'\n");
        fakeLogs.append("... Connecting to remote host...\n");
        fakeLogs.append("... Authenticating with credentials...\n");
        if (command.contains("docker build")) {
            fakeLogs.append("... Step 1/3 : Sending build context to Docker daemon...\n");
            fakeLogs.append("... Step 2/3 : Building image...\n");
            fakeLogs.append("... Step 3/3 : Successfully tagged image 'myapp:latest'...\n");
        } else if (command.contains("kubectl apply")) {
            fakeLogs.append("... Reading configuration file...\n");
            fakeLogs.append("... Creating resources on cluster...\n");
            fakeLogs.append("... service/myapp created\n");
            fakeLogs.append("... deployment.apps/myapp configured\n");
        }
        fakeLogs.append("... Cleaning up temporary files...\n");
        fakeLogs.append("SIMULATION COMPLETE: Execution finished successfully.\n");

        return fakeLogs.toString();
    }
}