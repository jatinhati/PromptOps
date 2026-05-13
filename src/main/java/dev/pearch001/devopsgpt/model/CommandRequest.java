package dev.pearch001.devopsgpt.model;

import jakarta.validation.constraints.NotBlank;

public record CommandRequest(@NotBlank(message = "Task description cannot be blank.") String task) {
}
