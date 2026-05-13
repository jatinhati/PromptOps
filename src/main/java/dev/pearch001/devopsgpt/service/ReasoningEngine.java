package dev.pearch001.devopsgpt.service;


import dev.pearch001.devopsgpt.model.CommandResponse;
import dev.pearch001.devopsgpt.model.DialogueState;
import dev.pearch001.devopsgpt.model.EnhancedChatResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReasoningEngine {
    private static final Logger logger = LoggerFactory.getLogger(ReasoningEngine.class);
    private final CommandService commandService;
    private final ChatClient chatClient;
    private final AwsToolExecutor awsToolExecutor;

    private static final String RAG_SYSTEM_PROMPT = """
        You are DevOpsGPT, a helpful AI assistant. Your user is asking a question about DevOps or Cloud topics.
        Use your existing knowledge and the information from the "DOCUMENTS" section to provide a detailed and accurate answer.
        Incorporate the chat history to understand the context of the conversation.

        DOCUMENTS:
        {documents}
        """;

    public ReasoningEngine(CommandService commandService, ChatClient.Builder chatClientBuilder, AwsToolExecutor awsToolExecutor) {
        this.commandService = commandService;
        this.chatClient = chatClientBuilder.build();
        this.awsToolExecutor = awsToolExecutor;
    }

    public EnhancedChatResponse reason(DialogueState state, String userInput, List<Document> context, List<Message> history) {
        logger.info("Reasoning with intent: {}", state.getCurrentIntent());

        // --- AWS Tool Usage ---
        String awsResult;
        switch (state.getCurrentIntent()) {
            case AWS_EC2_START_INSTANCE:
                String startInstanceId = (String) state.getSlots().get("instanceId");
                if (startInstanceId == null) return new EnhancedChatResponse("Please provide an instance ID (e.g., i-12345abcdef).", List.of());
                awsResult = awsToolExecutor.startEc2Instance(startInstanceId);
                return new EnhancedChatResponse(awsResult, List.of());

            case AWS_EC2_STOP_INSTANCE:
                String stopInstanceId = (String) state.getSlots().get("instanceId");
                if (stopInstanceId == null) return new EnhancedChatResponse("Please provide an instance ID (e.g., i-12345abcdef).", List.of());
                awsResult = awsToolExecutor.stopEc2Instance(stopInstanceId);
                return new EnhancedChatResponse(awsResult, List.of());

            case AWS_S3_LIST_BUCKETS:
                awsResult = awsToolExecutor.listS3Buckets();
                return new EnhancedChatResponse(awsResult, List.of());

            case AWS_CLOUDWATCH_GET_METRICS:
                String cwInstanceId = (String) state.getSlots().get("instanceId");
                if (cwInstanceId == null) return new EnhancedChatResponse("Which instance ID do you want to get metrics for?", List.of());
                awsResult = awsToolExecutor.getCloudWatchCpuUtilization(cwInstanceId);
                return new EnhancedChatResponse(awsResult, List.of());

            case GENERATE_COMMAND:
                // Tool Use: If the intent is to generate a command, use the specialized CommandService.
                String task = (String) state.getSlots().getOrDefault("task", userInput);
                CommandResponse commandResponse = commandService.generateCommand(task);
                String reply = String.format("Here is the command for your task:\n\n**Command:**\n```sh\n%s\n```\n**Explanation:**\n%s",
                        commandResponse.command(), commandResponse.explanation());
                return new EnhancedChatResponse(reply, List.of()); // No RAG documents used

            case GENERAL_QUERY:
            default:
                // Fallback to RAG: For general questions, use the RAG pipeline.
                return executeRagQuery(userInput, context, history);
        }
    }

    private EnhancedChatResponse executeRagQuery(String userInput, List<Document> context, List<Message> history) {
        String documentsText = context.stream()
                                .map(Document::getText)
                                .collect(Collectors.joining("\n---\n"));

        List<String> sourceDocuments = context.stream()
                .map(doc -> doc.getMetadata().get("source").toString())
                .distinct().toList();

        logger.info("Executing RAG query with {} context documents.", context.size());

        // Build a prompt that includes history and RAG context
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(RAG_SYSTEM_PROMPT);
        Message systemMessage = systemPromptTemplate.createMessage(Map.of("documents", documentsText));

        // Create a new prompt object starting with the system message
        var promptBuilder = chatClient.prompt()
                .system(systemMessage.getText());

        // Add the entire message history
        history.forEach(promptBuilder::messages);

        // Add the current user message
        String responseContent = promptBuilder.user(userInput)
                .call()
                .content();

        logger.info("RAG response generated: {}", responseContent);

        return new EnhancedChatResponse(responseContent, sourceDocuments);
    }
}