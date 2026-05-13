package dev.pearch001.devopsgpt.service;

import dev.pearch001.devopsgpt.model.DialogueState;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DialogueStateTracker {

    private final Map<String, DialogueState> stateMap = new ConcurrentHashMap<>();
    // Pattern to find EC2 instance IDs (i-xxxxxxxxxxxxxxxxx)
    private static final Pattern INSTANCE_ID_PATTERN = Pattern.compile("(i-[a-f0-9]{17}|i-[a-f0-9]{8})");


    public DialogueState trackState(String sessionId, String userInput) {
        DialogueState state = stateMap.computeIfAbsent(sessionId, k -> new DialogueState());

        String lowerInput = userInput.toLowerCase();

        // Simple rule-based intent recognition (can be replaced with an LLM call for more accuracy)
        if (lowerInput.contains("start") && lowerInput.contains("ec2 instance")) {
            state.setCurrentIntent(DialogueState.Intent.AWS_EC2_START_INSTANCE);
            extractInstanceId(state, userInput);
        } else if (lowerInput.contains("stop") && lowerInput.contains("ec2 instance")) {
            state.setCurrentIntent(DialogueState.Intent.AWS_EC2_STOP_INSTANCE);
            extractInstanceId(state, userInput);
        } else if (lowerInput.contains("list") && lowerInput.contains("s3 buckets")) {
            state.setCurrentIntent(DialogueState.Intent.AWS_S3_LIST_BUCKETS);
        } else if ((lowerInput.contains("cpu") || lowerInput.contains("utilization")) && lowerInput.contains("instance")) {
            state.setCurrentIntent(DialogueState.Intent.AWS_CLOUDWATCH_GET_METRICS);
            extractInstanceId(state, userInput);
        }else if (lowerInput.startsWith("generate command to") || lowerInput.startsWith("how do i")) {
            state.setCurrentIntent(DialogueState.Intent.GENERATE_COMMAND);
            state.addSlot("task", userInput);
        } else if (lowerInput.startsWith("what is") || lowerInput.startsWith("explain")) {
            state.setCurrentIntent(DialogueState.Intent.GENERAL_QUERY);
        } else {
            state.setCurrentIntent(DialogueState.Intent.GENERAL_QUERY); // Default to RAG
        }

        return state;
    }


    private void extractInstanceId(DialogueState state, String userInput) {
        Matcher matcher = INSTANCE_ID_PATTERN.matcher(userInput);
        if (matcher.find()) {
            String instanceId = matcher.group(0);
            state.addSlot("instanceId", instanceId);
        }
    }
}