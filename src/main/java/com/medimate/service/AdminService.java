package com.medimate.service;

import com.medimate.dao.PostDAO;
import com.medimate.dao.UserDAO;
import com.medimate.model.Post;
import com.medimate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AdminService - Business logic for admin panel
 * Migrated from Spring Boot AdminController
 */
public class AdminService {

    private final UserDAO userDAO = new UserDAO();
    private final PostDAO postDAO = new PostDAO();

    /**
     * Get dashboard statistics
     */
    public Map<String, Long> getStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalUsers", userDAO.count());
        stats.put("totalPosts", postDAO.count());
        stats.put("totalDoctors", userDAO.countByRole("doctor"));
        stats.put("totalPatients", userDAO.countByRole("patient"));
        return stats;
    }

    /**
     * Get all users
     */
    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    /**
     * Delete a user by ID
     */
    public void deleteUser(int id) {
        userDAO.deleteById(id);
    }

    /**
     * Get all posts
     */
    public List<Post> getAllPosts() {
        return postDAO.findAll();
    }

    /**
     * Delete a post by ID
     */
    public void deletePost(int id) {
        postDAO.deleteById(id);
    }
}
