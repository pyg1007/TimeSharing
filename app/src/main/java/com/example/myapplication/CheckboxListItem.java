package com.example.myapplication;

public class CheckboxListItem {

    private String userid;
    private boolean selected;

    public CheckboxListItem(String id){
        this.userid = id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
