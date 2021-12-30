package com.example.khronos.structures;

public class User {

    private String id;
    private String user;
    private String email;
    private String avatar;

    public User(String id, String user, String email, String avatar) {
        this.id = id;
        this.user = user;
        this.email = email;
        this.avatar = (avatar.length() >= 1) ? avatar : null; // če ni avatarja
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
