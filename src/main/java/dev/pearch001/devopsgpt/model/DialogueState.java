package dev.pearch001.devopsgpt.model;

import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class DialogueState {

    public enum Intent {
        GENERAL_QUERY,
        GENERATE_COMMAND,
        SIMULATE_COMMAND,
        AWS_EC2_START_INSTANCE,
        AWS_S3_LIST_BUCKETS,
        AWS_CLOUDWATCH_GET_METRICS,
        AWS_EC2_STOP_INSTANCE, UNKNOWN
    }

    @Setter
    private Intent currentIntent = Intent.UNKNOWN;
    private final Map<String, Object> slots = new HashMap<>();
    public Intent getCurrentIntent() { return currentIntent; }
    public Map<String, Object> getSlots() { return slots; }
    public void addSlot(String key, Object value) { this.slots.put(key, value); }
}
