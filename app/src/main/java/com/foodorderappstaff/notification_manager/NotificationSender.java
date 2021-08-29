package com.foodorderappstaff.notification_manager;

public class NotificationSender {
    public Data data; // message data. like title, message
    public String to; // user token id

    public NotificationSender(Data data, String to) {
        this.data = data;
        this.to = to;
    }

    public NotificationSender() {
    }
}
