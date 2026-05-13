package dev.pearch001.devopsgpt.model;

import jakarta.validation.constraints.NotBlank;

public record ChatRequest(@NotBlank(message = "User message cannot be blank.") String message, @NotBlank(message = "Session ID cannot be blank.") String sessionId) {
}
