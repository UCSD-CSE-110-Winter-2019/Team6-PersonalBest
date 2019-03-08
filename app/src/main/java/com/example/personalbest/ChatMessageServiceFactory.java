package com.example.personalbest;

public class ChatMessageServiceFactory extends Factory<ChatMessageService> {
    private static ChatMessageServiceFactory instance;

    public static ChatMessageServiceFactory getInstance() {
        if (instance == null) {
            instance = new ChatMessageServiceFactory();
        }
        return instance;
    }


}
