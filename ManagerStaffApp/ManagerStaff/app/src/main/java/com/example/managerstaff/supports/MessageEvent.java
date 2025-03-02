package com.example.managerstaff.supports;

import com.example.managerstaff.models.NotificationData;

public class MessageEvent {
    public NotificationData notificationData;

    public MessageEvent(NotificationData notificationData) {
        this.notificationData = notificationData;
    }
}
