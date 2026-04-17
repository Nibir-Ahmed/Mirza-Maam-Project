package com.medimate.service;

import com.medimate.dao.UserDAO;
import com.medimate.model.User;
import com.medimate.util.PasswordUtil;
import com.medimate.util.SessionManager;

/**
 * AuthService - Handles user registration and login
 * Migrated from Spring Boot AuthService + AuthController
 */
public class AuthService {

    private final UserDAO userDAO = new UserDAO();

    /**
     * Register a new user
     * @return registered User or null if email exists
     */
    public User register(String name, String email, String password, String role) {
        // Check if email already exists
        User existing = userDAO.findByEmail(email);
        if (existing != null) {
            return null; // Email already taken
        }

        // Hash password with SHA-256
        String hashedPassword = PasswordUtil.hashPassword(password);

        // Create and save user
        User user = new User(name, email, hashedPassword, role);
        return userDAO.save(user);
    }

    /**
     * Login a user
     * @return User if credentials match, null otherwise
     */
    public User login(String email, String password) {
        User user = userDAO.findByEmail(email);
        if (user == null) {
            return null; // User not found
        }

        // Verify password
        if (!PasswordUtil.verifyPassword(password, user.getPassword())) {
            return null; // Invalid password
        }

        // Set session
        SessionManager.getInstance().login(user);
        return user;
    }

    /**
     * Logout current user
     */
    public void logout() {
        SessionManager.getInstance().logout();
    }
}
