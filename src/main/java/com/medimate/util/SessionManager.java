package com.medimate.util;

import com.medimate.model.User;

/**
 * SessionManager - Singleton pattern to manage current logged-in user state
 * Replaces web app's localStorage for user session
 */
public class SessionManager {

    private static SessionManager instance;
    private User currentUser;

    // Private constructor (Singleton pattern)
    private SessionManager() {}

    /**
     * Get the single instance of SessionManager
     */
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /**
     * Set the current logged-in user
     */
    public void login(User user) {
        this.currentUser = user;
    }

    /**
     * Clear the current user (logout)
     */
    public void logout() {
        this.currentUser = null;
    }

    /**
     * Get the currently logged-in user
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Check if a user is logged in
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Check if current user is admin
     */
    public boolean isAdmin() {
        return currentUser != null && "admin".equals(currentUser.getRole());
    }

    /**
     * Check if current user is a doctor
     */
    public boolean isDoctor() {
        return currentUser != null && "doctor".equals(currentUser.getRole());
    }

    /**
     * Check if current user is a patient
     */
    public boolean isPatient() {
        return currentUser != null && "patient".equals(currentUser.getRole());
    }
}
