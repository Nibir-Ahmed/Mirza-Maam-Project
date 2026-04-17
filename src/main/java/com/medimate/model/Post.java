package com.medimate.model;

import java.sql.Timestamp;

/**
 * Post model class for health blog posts - demonstrates Encapsulation
 */
public class Post {
    private int id;
    private String title;
    private String content;
    private String authorName;
    private int authorId;
    private Timestamp createdAt;

    // Default constructor
    public Post() {}

    // Constructor for creating new post
    public Post(String title, String content, String authorName, int authorId) {
        this.title = title;
        this.content = content;
        this.authorName = authorName;
        this.authorId = authorId;
    }

    // Full constructor
    public Post(int id, String title, String content, String authorName, int authorId, Timestamp createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.authorName = authorName;
        this.authorId = authorId;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public int getAuthorId() { return authorId; }
    public void setAuthorId(int authorId) { this.authorId = authorId; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return title + " by " + authorName;
    }
}
