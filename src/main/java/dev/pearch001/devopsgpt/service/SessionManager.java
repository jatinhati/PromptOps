package dev.pearch001.devopsgpt.service;

import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionManager {

    private final Map<String, List<Message>> sessionHistory = new ConcurrentHashMap<>();

    public void addMessage(String sessionId, Message message) {
        sessionHistory.computeIfAbsent(sessionId, k -> new ArrayList<>()).add(message);
    }

    public List<Message> getHistory(String sessionId) {
        return sessionHistory.getOrDefault(sessionId, new ArrayList<>());
    }
}
