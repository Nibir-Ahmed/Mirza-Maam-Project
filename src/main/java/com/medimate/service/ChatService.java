package com.medimate.service;

import com.medimate.dao.ChatMessageDAO;
import com.medimate.dao.UserDAO;
import com.medimate.model.ChatMessage;
import com.medimate.model.User;

import java.util.List;

/**
 * ChatService - Business logic for doctor-patient messaging
 * Replaces WebSocket-based chat with database-backed messaging
 */
public class ChatService {

    private final ChatMessageDAO chatMessageDAO = new ChatMessageDAO();
    private final UserDAO userDAO = new UserDAO();

    /**
     * Send a message
     */
    public ChatMessage sendMessage(int senderId, int receiverId, String messageText) {
        ChatMessage message = new ChatMessage(senderId, receiverId, messageText);
        return chatMessageDAO.save(message);
    }

    /**
     * Get all messages between two users
     */
    public List<ChatMessage> getMessages(int userId1, int userId2) {
        return chatMessageDAO.findMessagesBetween(userId1, userId2);
    }

    /**
     * Get new messages after a certain message ID (for auto-refresh polling)
     */
    public List<ChatMessage> getNewMessages(int userId1, int userId2, int afterId) {
        return chatMessageDAO.findNewMessages(userId1, userId2, afterId);
    }

    /**
     * Get all users except the current user (for chat user list)
     */
    public List<User> getAvailableUsers(int currentUserId) {
        return userDAO.findAllExcept(currentUserId);
    }
}
