package com.medimate.service;

import com.medimate.dao.PostDAO;
import com.medimate.model.Post;

import java.util.List;

/**
 * PostService - Business logic for blog posts
 * Migrated from Spring Boot PostService + PostController
 */
public class PostService {

    private final PostDAO postDAO = new PostDAO();

    /**
     * Get all blog posts
     */
    public List<Post> getAllPosts() {
        return postDAO.findAll();
    }

    /**
     * Get recent posts (for home page)
     */
    public List<Post> getRecentPosts(int limit) {
        return postDAO.findRecent(limit);
    }

    /**
     * Create a new post
     */
    public Post createPost(String title, String content, String authorName, int authorId) {
        Post post = new Post(title, content, authorName, authorId);
        return postDAO.save(post);
    }

    /**
     * Delete a post
     */
    public void deletePost(int id) {
        postDAO.deleteById(id);
    }

    /**
     * Get total post count
     */
    public long getPostCount() {
        return postDAO.count();
    }
}
