package com.medimate.dao;

import com.medimate.model.Post;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * PostDAO - Data Access Object for blog Post entity
 */
public class PostDAO {

    private Connection getConnection() {
        return DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Save a new post
     */
    public Post save(Post post) {
        String sql = "INSERT INTO posts (title, content, author_name, created_at) VALUES (?, ?, ?, NOW())";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, post.getTitle());
            stmt.setString(2, post.getContent());
            stmt.setString(3, post.getAuthorName());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                post.setId(rs.getInt(1));
            }
            return post;
        } catch (SQLException e) {
            System.err.println("Error saving post: " + e.getMessage());
            return null;
        }
    }

    /**
     * Get all posts (newest first)
     */
    public List<Post> findAll() {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT * FROM posts ORDER BY created_at DESC";
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                posts.add(mapResultSetToPost(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching posts: " + e.getMessage());
        }
        return posts;
    }

    /**
     * Get recent posts (limited)
     */
    public List<Post> findRecent(int limit) {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT * FROM posts ORDER BY created_at DESC LIMIT ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                posts.add(mapResultSetToPost(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching recent posts: " + e.getMessage());
        }
        return posts;
    }

    /**
     * Delete post by ID
     */
    public void deleteById(int id) {
        String sql = "DELETE FROM posts WHERE id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting post: " + e.getMessage());
        }
    }

    /**
     * Count total posts
     */
    public long count() {
        String sql = "SELECT COUNT(*) FROM posts";
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) {
            System.err.println("Error counting posts: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Map ResultSet to Post object
     */
    private Post mapResultSetToPost(ResultSet rs) throws SQLException {
        return new Post(
            rs.getInt("id"),
            rs.getString("title"),
            rs.getString("content"),
            rs.getString("author_name"),
            0,
            rs.getTimestamp("created_at")
        );
    }
}
