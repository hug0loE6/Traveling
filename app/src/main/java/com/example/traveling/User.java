package com.example.traveling;

public class User {

    private String firstname;
    private String lastname;
    private int avatarResId;

    public User(String firstname, String lastname, int avatarResId) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.avatarResId = avatarResId;
    }

    public String getFirstname() { return firstname; }
    public String getLastname() { return lastname; }
    public int getAvatarResId() { return avatarResId; }
}