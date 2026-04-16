package com.medimate.model;

import java.sql.Timestamp;

/**
 * ChatMessage model for doctor-patient messaging - demonstrates Encapsulation
 */
public class ChatMessage {
    private int id;
    private int senderId;
    private int receiverId;
    private String senderName;
    private String receiverName;
    private String message;
    private Timestamp sentAt;

    // Default constructor
    public ChatMessage() {}

    // Constructor for sending messages
    public ChatMessage(int senderId, int receiverId, String message) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
    }

    // Full constructor
    public ChatMessage(int id, int senderId, int receiverId, String senderName,
                       String receiverName, String message, Timestamp sentAt) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.message = message;
        this.sentAt = sentAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }

    public int getReceiverId() { return receiverId; }
    public void setReceiverId(int receiverId) { this.receiverId = receiverId; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Timestamp getSentAt() { return sentAt; }
    public void setSentAt(Timestamp sentAt) { this.sentAt = sentAt; }

    @Override
    public String toString() {
        return senderName + ": " + message;
    }
}
