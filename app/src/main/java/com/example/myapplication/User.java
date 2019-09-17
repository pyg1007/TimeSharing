package com.example.myapplication;


public class User {

    private String userID;

    public String getUserID() {
        return userID;
    }


    public void setUserID(String userID) {
        this.userID = userID;
    }

    public User(String userID) {
        this.userID = userID;
    }
}