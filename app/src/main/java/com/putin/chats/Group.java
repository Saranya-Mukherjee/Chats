package com.putin.chats;

public class Group {

    String people, name, imageURL, id;

    public Group(String people, String name, String imageURL, String id) {
        this.people = people;
        this.name = name;
        this.imageURL = imageURL;
        this.id = id;
    }

    public Group() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPeople() {
        return people;
    }

    public void setPeople(String people) {
        this.people = people;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
