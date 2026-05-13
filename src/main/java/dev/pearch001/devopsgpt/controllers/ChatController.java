package dev.pearch001.devopsgpt.controllers;

// src/main/java/com/devopsgpt/controller/ChatController.java

import dev.pearch001.devopsgpt.model.ChatRequest;
import dev.pearch001.devopsgpt.model.ChatResponse;
import dev.pearch001.devopsgpt.model.EnhancedChatResponse;
import dev.pearch001.devopsgpt.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("Pong! DevOpsGPT is running.");
    }

    /**
     * Handles a standard chat message without RAG.
     */
    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest chatRequest) {
        String reply = chatService.getChatReply(chatRequest.message());
        return ResponseEntity.ok(new ChatResponse(reply, chatRequest.sessionId()));
    }

    /**
     * Handles a chat message using Retrieval-Augmented Generation (RAG).
     * It finds relevant documents and uses them to provide a more accurate answer.
     * @param chatRequest The user's message.
     * @return A ChatResponse containing the context-aware AI reply.
     */
    @PostMapping("/chat/rag")
    public ResponseEntity<ChatResponse> ragChat(@Valid @RequestBody ChatRequest chatRequest) {
        String reply = chatService.getRagReply(chatRequest.message());
        return ResponseEntity.ok(new ChatResponse(reply, chatRequest.sessionId()));
    }

    /**
     * The primary, stateful chat endpoint.
     */
    @PostMapping("/chat/advanced") // New endpoint for the advanced logic
    public ResponseEntity<EnhancedChatResponse> advancedChat(@Valid @RequestBody ChatRequest chatRequest) {
        EnhancedChatResponse response = chatService.getAdvancedReply(
                chatRequest.sessionId(),
                chatRequest.message()
        );
        return ResponseEntity.ok(response);
    }
}
