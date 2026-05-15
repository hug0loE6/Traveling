package com.example.traveling;

public class Post {
    private String username;
    private String content;
    private int avatarResId; // ressource drawable pour l'avatar

    public Post(String username, String content, int avatarResId) {
        this.username = username;
        this.content = content;
        this.avatarResId = avatarResId;
    }

    // Getters
    public String getUsername() { return username; }
    public String getContent() { return content; }
    public int getAvatarResId() { return avatarResId; }
}