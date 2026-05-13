package dev.pearch001.devopsgpt.model;

import java.util.List;

public record EnhancedChatResponse(
    String response,
    List<String> sourceDocuments
) {

}
