package com.example.traveling;

public class Post {

    private String username;
    private String content;
    private int avatarResId;

    // NEW (minimum viable travel app)
    private String location;
    private String date;

    public Post(String username, String content, int avatarResId, String location, String date) {
        this.username = username;
        this.content = content;
        this.avatarResId = avatarResId;
        this.location = location;
        this.date = date;
    }

    public String getUsername() { return username; }
    public String getContent() { return content; }
    public int getAvatarResId() { return avatarResId; }
    public String getLocation() { return location; }
    public String getDate() { return date; }
}