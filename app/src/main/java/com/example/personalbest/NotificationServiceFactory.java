package com.example.personalbest;

import com.example.personalbest.Factory;

public class NotificationServiceFactory extends Factory<NotificationService> {
    private static NotificationServiceFactory instance;

    public static NotificationServiceFactory getInstance() {
        if (instance == null) {
            instance = new NotificationServiceFactory();
        }
        return instance;
    }
}