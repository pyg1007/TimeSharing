package com.example.myapplication;

import android.view.View;

public class User {

    private String userID;
    private View.OnClickListener onClickListener;

    public String getUserID() {
        return userID;
    }

    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public User(String userID, View.OnClickListener onClickListener1) {
        this.userID = userID;
        this.onClickListener = onClickListener1;
    }
}