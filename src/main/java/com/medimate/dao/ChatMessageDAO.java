package com.medimate.dao;

import com.medimate.model.ChatMessage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ChatMessageDAO - Data Access Object for chat messages
 * Replaces WebSocket with database-backed messaging
 */
public class ChatMessageDAO {

    private Connection getConnection() {
        return DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Save a new chat message
     */
    public ChatMessage save(ChatMessage message) {
        String sql = "INSERT INTO chat_messages (sender_id, receiver_id, message) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, message.getSenderId());
            stmt.setInt(2, message.getReceiverId());
            stmt.setString(3, message.getMessage());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                message.setId(rs.getInt(1));
            }
            return message;
        } catch (SQLException e) {
            System.err.println("Error saving message: " + e.getMessage());
            return null;
        }
    }

    /**
     * Get messages between two users (both directions)
     */
    public List<ChatMessage> findMessagesBetween(int userId1, int userId2) {
        List<ChatMessage> messages = new ArrayList<>();
        String sql = "SELECT cm.*, " +
                     "s.name AS sender_name, r.name AS receiver_name " +
                     "FROM chat_messages cm " +
                     "JOIN users s ON cm.sender_id = s.id " +
                     "JOIN users r ON cm.receiver_id = r.id " +
                     "WHERE (cm.sender_id = ? AND cm.receiver_id = ?) " +
                     "   OR (cm.sender_id = ? AND cm.receiver_id = ?) " +
                     "ORDER BY cm.sent_at ASC";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, userId1);
            stmt.setInt(2, userId2);
            stmt.setInt(3, userId2);
            stmt.setInt(4, userId1);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                messages.add(mapResultSetToMessage(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching messages: " + e.getMessage());
        }
        return messages;
    }

    /**
     * Get new messages after a certain ID (for polling/auto-refresh)
     */
    public List<ChatMessage> findNewMessages(int userId1, int userId2, int afterId) {
        List<ChatMessage> messages = new ArrayList<>();
        String sql = "SELECT cm.*, " +
                     "s.name AS sender_name, r.name AS receiver_name " +
                     "FROM chat_messages cm " +
                     "JOIN users s ON cm.sender_id = s.id " +
                     "JOIN users r ON cm.receiver_id = r.id " +
                     "WHERE cm.id > ? " +
                     "AND ((cm.sender_id = ? AND cm.receiver_id = ?) " +
                     "  OR (cm.sender_id = ? AND cm.receiver_id = ?)) " +
                     "ORDER BY cm.sent_at ASC";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, afterId);
            stmt.setInt(2, userId1);
            stmt.setInt(3, userId2);
            stmt.setInt(4, userId2);
            stmt.setInt(5, userId1);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                messages.add(mapResultSetToMessage(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching new messages: " + e.getMessage());
        }
        return messages;
    }

    /**
     * Map ResultSet to ChatMessage
     */
    private ChatMessage mapResultSetToMessage(ResultSet rs) throws SQLException {
        return new ChatMessage(
            rs.getInt("id"),
            rs.getInt("sender_id"),
            rs.getInt("receiver_id"),
            rs.getString("sender_name"),
            rs.getString("receiver_name"),
            rs.getString("message"),
            rs.getTimestamp("sent_at")
        );
    }
}
