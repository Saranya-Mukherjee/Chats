package com.putin.chats;

public class User {

    String id, username, imageURL, pno;

    public User(String id, String username, String imageURL, String pno) {
        this.id = id;
        this.username = username;
        this.imageURL = imageURL;
        this.pno = pno;
    }

    public User() {
    }

    public String getPno() {
        return pno;
    }

    public void setPno(String pno) {
        this.pno = pno;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
