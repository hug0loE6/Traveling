package com.example.traveling;

public class Post {

    private String username;
    private String description;
    private String location;
    private String period;

    private int avatarResId;
    private boolean liked = false;
    private String imageUri;

    public Post(String username,
                String description,
                String location,
                String period,
                String imageUri,
                int avatarResId) {

        this.username = username;
        this.description = description;
        this.location = location;
        this.period = period;
        this.imageUri = imageUri;
        this.avatarResId = avatarResId;
    }

    public String getUsername() { return username; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public String getPeriod() { return period; }
    public String getImageUri() { return imageUri; }
    public void setImageUri(String image) {imageUri=image;}
    public int getAvatarResId() { return avatarResId; }

    public boolean isLiked() { return liked; }

    public void toggleLike() {
        liked = !liked;
    }
}