package com.example.personalbest;

import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class MockMessageAdapter implements ChatMessageService {
    SaveLocal saveLocal;
    public HashMap<String, Map<String, String>> chat;

    public MockMessageAdapter() {
        chat = new HashMap<>();
    }

    @Override
    public Task<?> addMessage(Map<String, String> message) {
        chat.put("msg1", message);
        return null;
    }

    @Override
    public void addOrderedMessagesListener(Consumer<List<ChatMessage>> listener) {

    }

    public String getMessages() {
        return chat.get("msg1").get("text");
    }
}

