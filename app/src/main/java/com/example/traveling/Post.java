package com.example.traveling;

public class Post {

    private String username;
    private String description;
    private String location;
    private String period;
    private int imageResId;
    private int avatarResId;
    private boolean liked = false;

    public Post(String username,
                String description,
                String location,
                String period,
                int imageResId,
                int avatarResId) {

        this.username = username;
        this.description = description;
        this.location = location;
        this.period = period;
        this.imageResId = imageResId;
        this.avatarResId = avatarResId;
    }

    public String getUsername() { return username; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public String getPeriod() { return period; }
    public int getImageResId() { return imageResId; }
    public int getAvatarResId() { return avatarResId; }

    public boolean isLiked() { return liked; }

    public void toggleLike() {
        liked = !liked;
    }
}